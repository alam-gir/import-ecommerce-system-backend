package com.importer_ecommerce.importEcommerce.modules.product.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for simplified product data
 * Used for product lists where full details are not needed
 */
public record ProductSummaryResponse(
    UUID id,
    String title,
    String description,
    String profileImage,
    Integer minimumOrderQuantity,
    CategoryInfo category,
    Integer variantCount,
    Integer totalStock,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
