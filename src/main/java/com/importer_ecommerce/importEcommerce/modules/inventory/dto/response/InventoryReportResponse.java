package com.importer_ecommerce.importEcommerce.modules.inventory.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for inventory reports
 * Contains comprehensive inventory summary data
 */
public record InventoryReportResponse(
    Period period,
    Summary summary
) {
    
    /**
     * Time period for the report
     */
    public record Period(
        LocalDateTime from,
        LocalDateTime to
    ) {}
    
    /**
     * Summary statistics for the period
     */
    public record Summary(
        Integer totalSales,
        Integer totalReturns,
        Integer totalAdjustments,
        Integer netStockChange,
        List<TopSellingVariant> topSellingVariants,
        List<SlowMovingVariant> slowMovingVariants
    ) {}
    
    /**
     * Top selling variant information
     */
    public record TopSellingVariant(
        UUID variantId,
        String variantSku,
        String productTitle,
        Integer salesQuantity,
        BigDecimal revenue
    ) {}
    
    /**
     * Slow moving variant information
     */
    public record SlowMovingVariant(
        UUID variantId,
        String variantSku,
        String productTitle,
        Integer salesQuantity,
        Integer daysInStock
    ) {}
}
