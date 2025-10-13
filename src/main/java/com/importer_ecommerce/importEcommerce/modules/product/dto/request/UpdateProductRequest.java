package com.importer_ecommerce.importEcommerce.modules.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Request DTO for updating a product
 * Contains all fields that can be updated for an existing product
 */
@Data
public class UpdateProductRequest {
    
    @NotBlank(message = "Product title is required")
    @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
    private String title;
    
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    
    @NotBlank(message = "Category is required")
    private String categoryId; // UUID as string for validation
    
    @Positive(message = "Minimum order quantity must be positive")
    private Integer minimumOrderQuantity;
    
    // Profile image for the product
    private MultipartFile profileImage;
    
    // Additional product images
    private List<MultipartFile> images;
    
    // Images for product description/details
    private List<MultipartFile> descriptionImages;
    
    // List of existing image URLs to keep (for partial updates)
    private List<String> existingImages;
    
    // List of existing description image URLs to keep
    private List<String> existingDescriptionImages;
}

