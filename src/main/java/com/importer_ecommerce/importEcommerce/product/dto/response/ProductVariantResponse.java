package com.importer_ecommerce.importEcommerce.product.dto.response;

import static com.importer_ecommerce.importEcommerce.product.entity.ProductVariant.InventoryTracking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProductVariantResponse(
    UUID id,
    String sku,
    String title,
    BigDecimal price,
    BigDecimal compareAtPrice,
    Integer inventoryQuantity,
    InventoryTracking inventoryTracking,
    Integer lowStockThreshold,
    String attributes,
    List<String> images,
    Boolean isActive,
    UUID productId,
    String productTitle,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
