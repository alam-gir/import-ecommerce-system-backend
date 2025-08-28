package com.importer_ecommerce.importEcommerce.product.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
public class UpdateCategoryRequest {
    
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    private MultipartFile profileImage;
    
    private MultipartFile coverImage;
    
    private UUID parentId;
}
