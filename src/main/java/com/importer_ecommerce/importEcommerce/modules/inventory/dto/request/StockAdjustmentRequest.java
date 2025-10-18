package com.importer_ecommerce.importEcommerce.modules.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for single stock adjustment
 * Used for manual stock adjustments with reason tracking
 */
@Data
public class StockAdjustmentRequest {
    
    @NotBlank(message = "Variant ID is required")
    private String variantId;
    
    @NotNull(message = "Adjustment type is required")
    private AdjustmentType adjustmentType;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    
    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
    
    @NotNull(message = "Created by user ID is required")
    private String createdBy;
    
    /**
     * Adjustment types for stock modifications
     */
    public enum AdjustmentType {
        ADD,        // Add stock
        REMOVE,     // Remove stock
        SET         // Set exact stock level
    }
}
