package com.importer_ecommerce.importEcommerce.modules.inventory.dto.response;

import java.util.UUID;

/**
 * Response DTO for stock adjustment results
 * Contains information about the adjustment operation
 */
public record StockAdjustmentResponse(
    UUID variantId,
    Integer previousStock,
    Integer newStock,
    String adjustmentType,
    Integer quantity,
    UUID movementId
) {}
