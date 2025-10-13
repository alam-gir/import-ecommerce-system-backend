package com.importer_ecommerce.importEcommerce.modules.product.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Request DTO for removing product description images
 */
@Data
public class RemoveProductDescriptionImagesRequest {
    
    @NotNull(message = "Description image URLs list is required")
    @NotEmpty(message = "At least one description image URL is required")
    private List<String> descriptionImageUrls;
}
