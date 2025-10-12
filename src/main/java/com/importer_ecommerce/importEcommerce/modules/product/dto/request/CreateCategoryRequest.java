package com.importer_ecommerce.importEcommerce.modules.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request DTO for creating a category
 */
@Data
public class CreateCategoryRequest {
    
    @NotBlank(message = "Category title is required")
    @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
    private String title;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private MultipartFile image;
}
