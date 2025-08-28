package com.importer_ecommerce.importEcommerce.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import static com.importer_ecommerce.importEcommerce.product.entity.Product.ProductStatus;

import java.util.List;
import java.util.UUID;

@Data
public class UpdateProductRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Slug is required")
    private String slug;
    
    private String description; // HTML content
    
    private String note;
    
    private String brand;
    
    private String attributes; // JSON string
    
    private ProductStatus status;
    
    private Boolean featured;
    
    private String seoTitle;
    
    private String seoDescription;
    
    private String seoKeywords;
    
    private List<UUID> categoryIds;
}
