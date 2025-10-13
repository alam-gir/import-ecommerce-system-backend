package com.importer_ecommerce.importEcommerce.modules.product.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for product specification data
 * Used when returning specification information separately
 */
public record ProductSpecificationResponse(
    UUID id,
    String name,
    String value,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

