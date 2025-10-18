package com.importer_ecommerce.importEcommerce.modules.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * Request DTO for bulk stock updates
 * Used to update multiple variants at once
 */
@Data
public class BulkStockUpdateRequest {
    
    @NotEmpty(message = "Updates list cannot be empty")
    private List<StockUpdateItem> updates;
    
    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
    
    @NotNull(message = "Created by user ID is required")
    private String createdBy;
    
    @Data
    public static class StockUpdateItem {
        
        @NotBlank(message = "Variant ID is required")
        private String variantId;
        
        @NotNull(message = "Stock quantity is required")
        @Positive(message = "Stock quantity must be positive")
        private Integer stockQuantity;
        
        @Positive(message = "Low stock threshold must be positive")
        private Integer lowStockThreshold;
        
        @Size(max = 500, message = "Reason must not exceed 500 characters")
        private String reason;
    }
}
