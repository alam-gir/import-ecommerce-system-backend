package com.importer_ecommerce.importEcommerce.modules.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for updating a product variant
 * Used to modify existing SKUs with price, stock, and attribute combinations
 */
@Data
public class UpdateProductVariantRequest {
    
    @NotBlank(message = "SKU is required")
    @Size(min = 3, max = 100, message = "SKU must be between 3 and 100 characters")
    private String sku;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    @Positive(message = "Compare at price must be positive")
    private BigDecimal compareAtPrice;
    
    @Positive(message = "Cost price must be positive")
    private BigDecimal costPrice;
    
    @NotNull(message = "Stock quantity is required")
    @Positive(message = "Stock quantity must be positive")
    private Integer stockQuantity;
    
    @Positive(message = "Low stock threshold must be positive")
    private Integer lowStockThreshold;
    
    @Positive(message = "Weight must be positive")
    private BigDecimal weight;
    
    @Size(max = 100, message = "Dimensions must not exceed 100 characters")
    private String dimensions; // Format: "LxWxH" in cm
    
    @Size(max = 50, message = "Barcode must not exceed 50 characters")
    private String barcode;
    
    private Boolean isActive;
    
    private Boolean isTracked;
    
    // List of attribute value IDs to link to this variant
    private List<String> attributeValueIds;
}

