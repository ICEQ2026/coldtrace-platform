package com.acme.coldtrace.platform.aiassistance.infrastructure.ai;

import com.acme.coldtrace.platform.aiassistance.application.commandservices.AiAssistanceFailure;
import com.acme.coldtrace.platform.aiassistance.application.model.AiGeneratedResponse;
import com.acme.coldtrace.platform.aiassistance.application.model.AiStructuredPrompt;
import com.acme.coldtrace.platform.aiassistance.application.ports.AiStructuredOutputPort;
import com.acme.coldtrace.platform.shared.application.result.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Spring AI adapter for validated structured output.
 *
 * @since 1.0
 */
@Slf4j
@Component
public class SpringAiStructuredOutputAdapter implements AiStructuredOutputPort {
    private final ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;
    private final Validator validator;
    private final AiAssistanceProperties properties;
    private final ExecutorService executorService;

    public SpringAiStructuredOutputAdapter(
            ObjectProvider<ChatClient.Builder> chatClientBuilderProvider,
            Validator validator,
            AiAssistanceProperties properties,
            ExecutorService aiAssistanceExecutorService
    ) {
        this.chatClientBuilderProvider = chatClientBuilderProvider;
        this.validator = validator;
        this.properties = properties;
        this.executorService = aiAssistanceExecutorService;
    }

    @Override
    public <T> Result<AiGeneratedResponse<T>, AiAssistanceFailure> requestStructuredOutput(
            AiStructuredPrompt prompt,
            Class<T> responseType) {
        if (!properties.enabled()) {
            log.info("AI assistance call skipped because provider is disabled");
            return Result.failure(new AiAssistanceFailure.ProviderDisabled());
        }
        if (!properties.hasSupportedProvider()) {
            log.warn("Unsupported AI provider configured: {}", properties.provider());
            return Result.failure(new AiAssistanceFailure.UnsupportedProvider(properties.provider()));
        }

        var chatClientBuilder = resolveChatClientBuilder();
        if (chatClientBuilder == null) {
            log.warn("No Spring AI chat client builder available for provider={}", properties.provider());
            return Result.failure(new AiAssistanceFailure.ProviderNotConfigured(properties.provider()));
        }

        var outputConverter = new BeanOutputConverter<>(responseType);
        var contentResult = requestProviderContent(chatClientBuilder, prompt, outputConverter);
        if (contentResult.isFailure()) {
            return Result.failure(contentResult.failure().orElseThrow());
        }

        var output = convertStructuredOutput(contentResult.success().orElse(""), outputConverter);
        if (output.isFailure()) {
            return Result.failure(output.failure().orElseThrow());
        }

        var validationFailure = validateOutput(output.success().orElseThrow());
        if (validationFailure != null) {
            return Result.failure(validationFailure);
        }

        return Result.success(new AiGeneratedResponse<>(
                properties.provider(),
                properties.modelName(),
                output.success().orElseThrow()
        ));
    }

    private ChatClient.Builder resolveChatClientBuilder() {
        try {
            return chatClientBuilderProvider.getIfAvailable();
        } catch (RuntimeException exception) {
            log.warn("Spring AI chat client builder resolution failed", exception);
            return null;
        }
    }

    private Result<String, AiAssistanceFailure> requestProviderContent(
            ChatClient.Builder chatClientBuilder,
            AiStructuredPrompt structuredPrompt,
            BeanOutputConverter<?> outputConverter) {
        var future = executorService.submit(() -> executeProviderCall(
                chatClientBuilder,
                structuredPrompt,
                outputConverter
        ));
        try {
            var content = future.get(Math.max(1L, properties.timeout().toMillis()), TimeUnit.MILLISECONDS);
            if (content == null || content.isBlank()) {
                return Result.failure(new AiAssistanceFailure.InvalidStructuredOutput("empty provider response"));
            }
            return Result.success(content);
        } catch (TimeoutException exception) {
            future.cancel(true);
            log.warn("AI provider request timed out: provider={}, model={}, timeout={}",
                    properties.provider(), properties.modelName(), properties.timeout());
            return Result.failure(new AiAssistanceFailure.ProviderTimeout());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            log.warn("AI provider request interrupted: provider={}, model={}",
                    properties.provider(), properties.modelName());
            return Result.failure(new AiAssistanceFailure.ProviderUnavailable());
        } catch (ExecutionException exception) {
            log.warn("AI provider request failed: provider={}, model={}",
                    properties.provider(), properties.modelName(), exception.getCause());
            return Result.failure(new AiAssistanceFailure.ProviderUnavailable());
        }
    }

    private String executeProviderCall(
            ChatClient.Builder chatClientBuilder,
            AiStructuredPrompt structuredPrompt,
            BeanOutputConverter<?> outputConverter) {
        var variables = new HashMap<>(structuredPrompt.variables());
        variables.put("format", outputFormatInstructions(outputConverter));
        var userMessage = PromptTemplate.builder()
                .template(structuredPrompt.userTemplate())
                .variables(variables)
                .build()
                .createMessage();
        var prompt = buildPrompt(
                outputConverter,
                new SystemMessage(structuredPrompt.systemInstruction()),
                userMessage
        );
        return chatClientBuilder.build()
                .prompt(prompt)
                .call()
                .content();
    }

    private String outputFormatInstructions(BeanOutputConverter<?> outputConverter) {
        if ("ollama".equals(properties.provider())) {
            return """
                    Return one JSON object matching the required response shape.
                    Fill every required field with values derived from the provided context.
                    Do not return the schema, examples, empty objects, or empty arrays.
                    """;
        }
        return outputConverter.getFormat();
    }

    private Prompt buildPrompt(
            BeanOutputConverter<?> outputConverter,
            SystemMessage systemMessage,
            Message userMessage) {
        var messages = List.of(systemMessage, userMessage);
        if (!"ollama".equals(properties.provider())) {
            return new Prompt(messages);
        }

        var options = OllamaChatOptions.builder()
                .model(properties.modelName())
                .format(outputConverter.getJsonSchemaMap())
                .build();
        return new Prompt(messages, options);
    }

    private <T> Result<T, AiAssistanceFailure> convertStructuredOutput(
            String content,
            BeanOutputConverter<T> outputConverter) {
        try {
            var output = outputConverter.convert(extractStructuredJson(content));
            if (output == null) {
                return Result.failure(new AiAssistanceFailure.InvalidStructuredOutput("empty structured output"));
            }
            return Result.success(output);
        } catch (RuntimeException exception) {
            log.warn("AI structured output conversion failed", exception);
            return Result.failure(new AiAssistanceFailure.InvalidStructuredOutput("conversion failed"));
        }
    }

    private String extractStructuredJson(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }

        var trimmedContent = content.trim();
        var firstJsonValue = extractFirstBalancedJsonValue(trimmedContent);
        return firstJsonValue == null ? trimmedContent : firstJsonValue;
    }

    private String extractFirstBalancedJsonValue(String content) {
        var start = firstJsonValueStart(content);
        if (start < 0) {
            return null;
        }

        var expectedClosings = new ArrayDeque<Character>();
        var inString = false;
        var escaping = false;

        for (int index = start; index < content.length(); index++) {
            var character = content.charAt(index);
            if (inString) {
                if (escaping) {
                    escaping = false;
                } else if (character == '\\') {
                    escaping = true;
                } else if (character == '"') {
                    inString = false;
                }
                continue;
            }

            if (character == '"') {
                inString = true;
            } else if (character == '{') {
                expectedClosings.push('}');
            } else if (character == '[') {
                expectedClosings.push(']');
            } else if ((character == '}' || character == ']') && !expectedClosings.isEmpty()
                    && expectedClosings.peek() == character) {
                expectedClosings.pop();
                if (expectedClosings.isEmpty()) {
                    return content.substring(start, index + 1).trim();
                }
            }
        }

        return null;
    }

    private int firstJsonValueStart(String content) {
        var objectStart = content.indexOf('{');
        var arrayStart = content.indexOf('[');
        if (objectStart < 0) {
            return arrayStart;
        }
        if (arrayStart < 0) {
            return objectStart;
        }
        return Math.min(objectStart, arrayStart);
    }

    private <T> AiAssistanceFailure validateOutput(T output) {
        var violations = validator.validate(output);
        if (violations.isEmpty()) {
            return null;
        }
        var details = violations.stream()
                .map(this::toViolationDetail)
                .sorted()
                .collect(Collectors.joining("; "));
        log.warn("AI structured output validation failed: {}", details);
        return new AiAssistanceFailure.InvalidStructuredOutput(details);
    }

    private String toViolationDetail(ConstraintViolation<?> violation) {
        return "%s %s".formatted(violation.getPropertyPath(), violation.getMessage());
    }
}
