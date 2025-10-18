package com.importer_ecommerce.importEcommerce.modules.inventory.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for out of stock items
 * Contains variant information for items with zero stock
 */
public record OutOfStockResponse(
    UUID productId,
    String productTitle,
    UUID variantId,
    String variantSku,
    LocalDateTime lastSold,
    Integer daysOutOfStock,
    BigDecimal averageDailySales
) {}
