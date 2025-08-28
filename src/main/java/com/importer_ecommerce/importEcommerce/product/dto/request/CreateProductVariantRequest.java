package com.importer_ecommerce.importEcommerce.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import static com.importer_ecommerce.importEcommerce.product.entity.ProductVariant.InventoryTracking;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateProductVariantRequest {
    
    @NotBlank(message = "SKU is required")
    private String sku;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    private BigDecimal compareAtPrice;
    
    @NotNull(message = "Inventory quantity is required")
    @Positive(message = "Inventory quantity must be positive")
    private Integer inventoryQuantity;
    
    @NotNull(message = "Inventory tracking is required")
    private InventoryTracking inventoryTracking;
    
    private Integer lowStockThreshold;
    
    private String attributes; // JSON string for variant-specific attributes
    
    private List<MultipartFile> images;
    
    private Boolean isActive = true;
}
