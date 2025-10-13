package com.importer_ecommerce.importEcommerce.modules.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for creating a variant attribute
 * Used to define attributes like Color, Size, Material for a product
 */
@Data
public class CreateVariantAttributeRequest {
    
    @NotBlank(message = "Attribute name is required")
    @Size(min = 2, max = 100, message = "Attribute name must be between 2 and 100 characters")
    private String name;
    
    @NotNull(message = "Attribute type is required")
    private String attributeType; // TEXT, IMAGE, NUMBER
}

