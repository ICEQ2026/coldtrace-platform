package com.acme.coldtrace.platform.aiassistance.infrastructure.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Locale;
import java.util.Set;

/**
 * Environment-driven AI assistance configuration.
 *
 * @param enabled whether AI assistance calls are enabled
 * @param provider configured provider name
 * @param modelName configured model name
 * @param timeout maximum time allowed for one provider request
 * @since 1.0
 */
@ConfigurationProperties(prefix = "coldtrace.ai")
public record AiAssistanceProperties(
        boolean enabled,
        String provider,
        String modelName,
        Duration timeout
) {
    private static final Set<String> SUPPORTED_PROVIDERS = Set.of("ollama", "openai");

    public AiAssistanceProperties {
        provider = normalizeProvider(provider);
        modelName = normalizeModelName(provider, modelName);
        timeout = normalizeTimeout(timeout);
    }

    /**
     * @return true when the configured provider is supported by the backend
     */
    public boolean hasSupportedProvider() {
        return SUPPORTED_PROVIDERS.contains(provider);
    }

    private static String normalizeProvider(String provider) {
        if (provider == null || provider.isBlank()) {
            return "ollama";
        }
        return provider.trim().toLowerCase(Locale.ROOT);
    }

    private static String normalizeModelName(String provider, String modelName) {
        if (modelName != null && !modelName.isBlank()) {
            return modelName.trim();
        }
        return switch (provider) {
            case "openai" -> "gpt-5.4-mini";
            case "ollama" -> "gemma3:4b";
            default -> "unknown";
        };
    }

    private static Duration normalizeTimeout(Duration timeout) {
        if (timeout == null || timeout.isZero() || timeout.isNegative()) {
            return Duration.ofSeconds(30);
        }
        return timeout;
    }
}
