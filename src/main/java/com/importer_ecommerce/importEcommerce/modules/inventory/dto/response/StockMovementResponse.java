package com.importer_ecommerce.importEcommerce.modules.inventory.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for stock movement history
 * Contains detailed information about stock changes
 */
public record StockMovementResponse(
    UUID id,
    UUID productId,
    String productTitle,
    UUID variantId,
    String variantSku,
    String movementType,
    Integer quantity,
    Integer previousStock,
    Integer newStock,
    String reason,
    String referenceId,
    LocalDateTime createdAt,
    String createdBy
) {}
