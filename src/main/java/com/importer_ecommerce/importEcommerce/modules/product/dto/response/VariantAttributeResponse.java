package com.importer_ecommerce.importEcommerce.modules.product.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for variant attribute data
 * Used when returning attribute information separately
 */
public record VariantAttributeResponse(
    UUID id,
    String name,
    String attributeType,
    List<VariantAttributeValueResponse> values,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
