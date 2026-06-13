package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request resource for updating an asset.
 *
 * @param locationId location identifier where the asset is placed
 * @param uuid asset unique identifier inside the organization
 * @param type business asset type
 * @param name asset display name
 * @param capacity asset capacity
 * @param description optional asset description
 * @param status asset operational status
 * @since 1.0
 */
@Schema(
        name = "UpdateAssetRequest",
        description = "Request payload for updating an organization-scoped cold-chain asset"
)
public record UpdateAssetResource(
        @NotNull(message = "is required")
        @Positive(message = "must be positive")
        @Schema(description = "Organization location identifier where the asset is placed", example = "1")
        Long locationId,

        @NotBlank(message = "is required")
        @Schema(description = "Asset business UUID unique inside the organization", example = "AST-FRZ-001")
        String uuid,

        @NotBlank(message = "is required")
        @Schema(description = "Business asset type", example = "FREEZER")
        String type,

        @NotBlank(message = "is required")
        @Schema(description = "Human-readable asset name", example = "Freezer A1")
        String name,

        @Positive(message = "must be positive")
        @Schema(description = "Asset storage or transport capacity", example = "450.0")
        Double capacity,

        @Schema(description = "Optional operational notes", example = "Main freezer for vaccines")
        String description,

        @NotBlank(message = "is required")
        @Schema(description = "Operational asset status", example = "active")
        String status
) {
}
