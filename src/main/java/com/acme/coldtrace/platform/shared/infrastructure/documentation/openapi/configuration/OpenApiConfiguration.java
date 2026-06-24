package com.acme.coldtrace.platform.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI metadata configuration.
 *
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfiguration {
    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI coldTraceOpenApi(
            @Value("${documentation.application.description:ColdTrace Platform REST API}") String applicationDescription,
            @Value("${documentation.application.version:1.0.0}") String applicationVersion
    ) {
        var info = new Info()
                .title("ColdTrace Platform API")
                .description(applicationDescription)
                .version(applicationVersion)
                .contact(new Contact()
                        .name("ICEQ ColdTrace Team"))
                .license(new License()
                        .name("MIT")
                        .url("https://opensource.org/license/mit"));

        var bearerSecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .info(info)
                .components(new Components().addSecuritySchemes(BEARER_AUTH_SCHEME, bearerSecurityScheme))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH_SCHEME));
    }
}
