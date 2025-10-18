package com.importer_ecommerce.importEcommerce.modules.product.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for product variant data
 * Used when returning variant information separately
 */
public record ProductVariantResponse(
    UUID id,
    String sku,
    BigDecimal price,
    BigDecimal compareAtPrice,
    BigDecimal costPrice,
    Integer stockQuantity,
    Integer lowStockThreshold,
    BigDecimal weight,
    String dimensions,
    String barcode,
    Boolean isActive,
    Boolean isTracked,
    List<VariantAttributeDetailResponse> attributeValues,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
