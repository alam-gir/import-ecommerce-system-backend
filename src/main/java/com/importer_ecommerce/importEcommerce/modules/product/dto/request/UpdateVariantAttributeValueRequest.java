package com.importer_ecommerce.importEcommerce.modules.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request DTO for updating a variant attribute value
 * Used to modify existing values like Red, Blue, Small, Large
 */
@Data
public class UpdateVariantAttributeValueRequest {
    
    @NotBlank(message = "Attribute value is required")
    @Size(min = 1, max = 255, message = "Attribute value must be between 1 and 255 characters")
    private String value;
    
    private MultipartFile image; // Image file for the attribute value (e.g., color swatches)
}
