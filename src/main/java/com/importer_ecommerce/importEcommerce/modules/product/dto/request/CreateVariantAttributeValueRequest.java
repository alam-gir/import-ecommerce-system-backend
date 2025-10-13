package com.importer_ecommerce.importEcommerce.modules.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request DTO for creating a variant attribute value
 * Used to add values like Red, Blue, Small, Large to an attribute
 */
@Data
public class CreateVariantAttributeValueRequest {
    
    @NotBlank(message = "Attribute value is required")
    @Size(min = 1, max = 255, message = "Attribute value must be between 1 and 255 characters")
    private String value;
    
    private MultipartFile image; // Image file for the attribute value (e.g., color swatches)
}
