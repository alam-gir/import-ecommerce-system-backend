package com.importer_ecommerce.importEcommerce.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import static com.importer_ecommerce.importEcommerce.product.entity.Product.ProductStatus;

import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.Min;

@Data
public class UpdateProductRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Slug is required")
    private String slug;
    
    private String description; // HTML content
    
    private String note;
    
    private String brand;
    
    @Min(value = 1, message = "Minimum order quantity must be at least 1")
    private Integer minOrderQuantity; // Optional for updates
    
    private String attributes; // JSON string
    
    private ProductStatus status;
    
    private Boolean featured;
    
    private String seoTitle;
    
    private String seoDescription;
    
    private String seoKeywords;
    
    private List<UUID> categoryIds;
}
