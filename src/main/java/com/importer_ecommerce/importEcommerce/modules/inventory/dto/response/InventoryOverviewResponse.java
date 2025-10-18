package com.importer_ecommerce.importEcommerce.modules.inventory.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for inventory overview dashboard
 * Contains overall inventory statistics
 */
public record InventoryOverviewResponse(
    Integer totalProducts,
    Integer totalVariants,
    BigDecimal totalStockValue,
    Integer lowStockItems,
    Integer outOfStockItems,
    Integer overstockItems,
    Integer totalStockQuantity,
    LocalDateTime lastUpdated
) {}
