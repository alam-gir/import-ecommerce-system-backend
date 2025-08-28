package com.importer_ecommerce.importEcommerce.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import static com.importer_ecommerce.importEcommerce.product.entity.Product.ProductStatus;

import java.util.List;
import java.util.UUID;

@Data
public class CreateProductRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Slug is required")
    private String slug;
    
    private String description; // HTML content
    
    private String note;
    
    private String brand;
    
    private String attributes; // JSON string
    
    @NotNull(message = "Status is required")
    private ProductStatus status;
    
    private Boolean featured = false;
    
    private String seoTitle;
    
    private String seoDescription;
    
    private String seoKeywords;
    
    private List<UUID> categoryIds;
}
