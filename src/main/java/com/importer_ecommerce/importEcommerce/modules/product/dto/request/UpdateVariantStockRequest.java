package com.importer_ecommerce.importEcommerce.modules.product.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Request DTO for updating variant stock
 * Used for quick stock updates without modifying other variant details
 */
@Data
public class UpdateVariantStockRequest {
    
    @NotNull(message = "Stock quantity is required")
    @Positive(message = "Stock quantity must be positive")
    private Integer stockQuantity;
    
    @Positive(message = "Low stock threshold must be positive")
    private Integer lowStockThreshold;
}

