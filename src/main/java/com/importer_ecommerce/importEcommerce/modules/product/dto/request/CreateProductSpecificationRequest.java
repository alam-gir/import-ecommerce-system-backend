package com.importer_ecommerce.importEcommerce.modules.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for creating a product specification
 * Used to add specifications like Material, Care instructions, etc.
 */
@Data
public class CreateProductSpecificationRequest {
    
    @NotBlank(message = "Specification name is required")
    @Size(min = 2, max = 255, message = "Specification name must be between 2 and 255 characters")
    private String name;
    
    @NotBlank(message = "Specification value is required")
    @Size(min = 1, max = 2000, message = "Specification value must be between 1 and 2000 characters")
    private String value;
}

