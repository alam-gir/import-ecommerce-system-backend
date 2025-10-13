package com.importer_ecommerce.importEcommerce.modules.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for updating a variant attribute
 * Used to modify existing attributes like Color, Size, Material
 */
@Data
public class UpdateVariantAttributeRequest {
    
    @NotBlank(message = "Attribute name is required")
    @Size(min = 2, max = 100, message = "Attribute name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Attribute type is required")
    private String attributeType; // TEXT, IMAGE, NUMBER
}

