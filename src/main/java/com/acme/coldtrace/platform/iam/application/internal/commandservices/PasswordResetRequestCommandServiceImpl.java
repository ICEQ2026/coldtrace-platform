package com.acme.coldtrace.platform.iam.application.internal.commandservices;

import com.acme.coldtrace.platform.iam.application.commandservices.PasswordResetConfirmationCommandResult;
import com.acme.coldtrace.platform.iam.application.commandservices.PasswordResetRequestCommandResult;
import com.acme.coldtrace.platform.iam.application.commandservices.PasswordResetRequestCommandService;
import com.acme.coldtrace.platform.iam.application.internal.outboundservices.email.PasswordResetEmailDeliveryService;
import com.acme.coldtrace.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.acme.coldtrace.platform.iam.domain.model.aggregates.PasswordResetRequest;
import com.acme.coldtrace.platform.iam.domain.model.commands.ConfirmPasswordResetCommand;
import com.acme.coldtrace.platform.iam.domain.model.commands.CreatePasswordResetRequestCommand;
import com.acme.coldtrace.platform.iam.domain.repositories.PasswordResetRequestRepository;
import com.acme.coldtrace.platform.iam.domain.repositories.UserRepository;
import com.acme.coldtrace.platform.shared.application.result.ApplicationError;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Application service implementation for password reset request operations.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class PasswordResetRequestCommandServiceImpl implements PasswordResetRequestCommandService {
    private static final int TOKEN_BYTE_LENGTH = 32;
    private static final Duration TOKEN_TTL = Duration.ofMinutes(30);
    private static final String DELIVERY_ACCEPTED = "REQUEST_ACCEPTED";
    private static final String INVALID_TOKEN = "identity-access.password-reset.error.token.invalid";

    private final UserRepository userRepository;
    private final PasswordResetRequestRepository passwordResetRequestRepository;
    private final PasswordResetEmailDeliveryService passwordResetEmailDeliveryService;
    private final HashingService hashingService;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetRequestCommandServiceImpl(
            UserRepository userRepository,
            PasswordResetRequestRepository passwordResetRequestRepository,
            PasswordResetEmailDeliveryService passwordResetEmailDeliveryService,
            HashingService hashingService
    ) {
        this.userRepository = userRepository;
        this.passwordResetRequestRepository = passwordResetRequestRepository;
        this.passwordResetEmailDeliveryService = passwordResetEmailDeliveryService;
        this.hashingService = hashingService;
    }

    /**
     * Handles a password reset request without revealing whether the email exists.
     *
     * @param command request command
     * @return generic accepted result or controlled application error
     */
    @Override
    @Transactional
    public Result<PasswordResetRequestCommandResult, ApplicationError> handle(CreatePasswordResetRequestCommand command) {
        var requestedAt = Instant.now();
        var expiresAt = requestedAt.plus(TOKEN_TTL);

        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty()) {
            log.info("Password reset requested for a non-existing email");
            return accepted(requestedAt, expiresAt);
        }

        try {
            var rawToken = generateResetToken();
            var tokenHash = hashResetToken(rawToken);
            var request = new PasswordResetRequest(
                    command.email(),
                    user.get().getId(),
                    tokenHash,
                    requestedAt,
                    expiresAt
            );
            passwordResetRequestRepository.save(request);
            var deliveryResult = passwordResetEmailDeliveryService.sendPasswordResetLink(
                    command.email(),
                    rawToken,
                    expiresAt
            );
            log.info(
                    "Password reset request accepted: userId={}, deliveryResult={}",
                    user.get().getId(),
                    deliveryResult
            );
            return accepted(requestedAt, expiresAt);
        } catch (NoSuchAlgorithmException exception) {
            log.error("Password reset token hashing failed", exception);
            return Result.failure(new ApplicationError(
                    "UNEXPECTED_ERROR",
                    "Password reset request could not be prepared",
                    "identity-access.password-reset.error.request-failed"
            ));
        }
    }

    /**
     * Handles password reset confirmation by consuming a valid token and changing the password hash.
     *
     * @param command confirmation command
     * @return confirmation result or controlled application error
     */
    @Override
    @Transactional
    public Result<PasswordResetConfirmationCommandResult, ApplicationError> handle(ConfirmPasswordResetCommand command) {
        try {
            var now = Instant.now();
            var tokenHash = hashResetToken(command.token());
            var request = passwordResetRequestRepository.findByTokenHash(tokenHash)
                    .orElse(null);

            if (request == null || request.isConsumed() || request.isExpiredAt(now)) {
                return invalidTokenFailure();
            }

            var user = userRepository.findById(request.getUserId()).orElse(null);
            if (user == null) {
                return invalidTokenFailure();
            }

            user.changePasswordHash(hashingService.encode(command.password()));
            userRepository.save(user);
            request.consume(now);
            passwordResetRequestRepository.save(request);
            log.info("Password reset confirmed: userId={}", user.getId());
            return Result.success(new PasswordResetConfirmationCommandResult(true));
        } catch (NoSuchAlgorithmException exception) {
            log.error("Password reset token hashing failed", exception);
            return Result.failure(new ApplicationError(
                    "UNEXPECTED_ERROR",
                    "Password reset confirmation could not be prepared",
                    "identity-access.password-reset.error.request-failed"
            ));
        }
    }

    private Result<PasswordResetRequestCommandResult, ApplicationError> accepted(
            Instant requestedAt,
            Instant expiresAt
    ) {
        return Result.success(new PasswordResetRequestCommandResult(
                true,
                requestedAt,
                expiresAt,
                DELIVERY_ACCEPTED
        ));
    }

    private static Result<PasswordResetConfirmationCommandResult, ApplicationError> invalidTokenFailure() {
        return Result.failure(ApplicationError.businessRuleViolation(INVALID_TOKEN));
    }

    private String generateResetToken() {
        var bytes = new byte[TOKEN_BYTE_LENGTH];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashResetToken(String rawToken) throws NoSuchAlgorithmException {
        var digest = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(digest.digest(rawToken.getBytes(StandardCharsets.UTF_8)));
    }
}
