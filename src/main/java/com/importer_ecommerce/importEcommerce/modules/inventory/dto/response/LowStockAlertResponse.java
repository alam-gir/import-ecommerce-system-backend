package com.importer_ecommerce.importEcommerce.modules.inventory.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for low stock alerts
 * Contains variant information with low stock details
 */
public record LowStockAlertResponse(
    UUID productId,
    String productTitle,
    UUID variantId,
    String variantSku,
    Integer currentStock,
    Integer lowStockThreshold,
    LocalDateTime lastSold,
    Integer daysUntilOutOfStock
) {}
