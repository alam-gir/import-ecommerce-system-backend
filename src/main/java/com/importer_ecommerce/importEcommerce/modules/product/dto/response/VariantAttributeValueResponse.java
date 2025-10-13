package com.importer_ecommerce.importEcommerce.modules.product.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for variant attribute value data
 * Used when returning attribute value information separately
 */
public record VariantAttributeValueResponse(
    UUID id,
    String value,
    String imageUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

