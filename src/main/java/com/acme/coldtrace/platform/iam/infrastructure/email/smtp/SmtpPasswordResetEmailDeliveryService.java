package com.acme.coldtrace.platform.iam.infrastructure.email.smtp;

import com.acme.coldtrace.platform.iam.application.internal.outboundservices.email.PasswordResetEmailDeliveryResult;
import com.acme.coldtrace.platform.iam.application.internal.outboundservices.email.PasswordResetEmailDeliveryService;
import com.acme.coldtrace.platform.iam.infrastructure.configuration.PasswordResetEmailProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * SMTP adapter for password reset emails.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class SmtpPasswordResetEmailDeliveryService implements PasswordResetEmailDeliveryService {
    private final JavaMailSender mailSender;
    private final PasswordResetEmailProperties properties;

    public SmtpPasswordResetEmailDeliveryService(
            JavaMailSender mailSender,
            PasswordResetEmailProperties properties
    ) {
        this.mailSender = mailSender;
        this.properties = properties;
    }

    @Override
    public PasswordResetEmailDeliveryResult sendPasswordResetLink(
            String recipientEmail,
            String rawToken,
            Instant expiresAt
    ) {
        if (!properties.hasRequiredConfiguration()) {
            log.info("Password reset email delivery is not configured");
            return PasswordResetEmailDeliveryResult.NOT_CONFIGURED;
        }

        try {
            var message = new SimpleMailMessage();
            message.setFrom(properties.emailFrom());
            message.setTo(recipientEmail);
            message.setSubject("ColdTrace password recovery");
            message.setText(buildEmailBody(buildResetLink(rawToken), expiresAt));
            mailSender.send(message);
            return PasswordResetEmailDeliveryResult.SENT;
        } catch (MailException exception) {
            log.warn("Password reset email delivery failed", exception);
            return PasswordResetEmailDeliveryResult.FAILED;
        }
    }

    private String buildResetLink(String rawToken) {
        var separator = properties.frontendResetUrl().contains("?") ? "&" : "?";
        return "%s%stoken=%s".formatted(
                properties.frontendResetUrl(),
                separator,
                URLEncoder.encode(rawToken, StandardCharsets.UTF_8)
        );
    }

    private static String buildEmailBody(String resetLink, Instant expiresAt) {
        return """
                We received a request to reset your ColdTrace password.

                Open this link to choose a new password:
                %s

                This link expires at %s UTC. If you did not request it, you can ignore this email.
                """.formatted(resetLink, expiresAt);
    }
}
