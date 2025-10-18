package com.importer_ecommerce.importEcommerce.modules.inventory.dto.response;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO for bulk stock update results
 * Contains information about successful and failed updates
 */
public record BulkStockUpdateResponse(
    Integer updatedCount,
    Integer failedCount,
    List<StockUpdateResult> updatedVariants,
    List<StockUpdateResult> failedVariants
) {
    
    /**
     * Individual stock update result
     */
    public record StockUpdateResult(
        UUID variantId,
        Integer previousStock,
        Integer newStock,
        String status,
        String errorMessage
    ) {}
}
