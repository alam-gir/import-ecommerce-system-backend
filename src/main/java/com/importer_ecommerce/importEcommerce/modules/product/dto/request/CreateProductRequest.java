package com.importer_ecommerce.importEcommerce.modules.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Request DTO for creating a product
 * Contains all basic product information needed for creation
 */
@Data
public class CreateProductRequest {
    
    @NotBlank(message = "Product title is required")
    @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
    private String title;
    
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    
    @NotNull(message = "Category is required")
    private String categoryId; // UUID as string for validation
    
    @Positive(message = "Minimum order quantity must be positive")
    private Integer minimumOrderQuantity;
    
    // Profile image for the product
    private MultipartFile profileImage;
    
    // Additional product images
    private List<MultipartFile> images;
    
    // Images for product description/details
    private List<MultipartFile> descriptionImages;
}

