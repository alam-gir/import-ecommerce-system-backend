package com.importer_ecommerce.importEcommerce.modules.product.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Request DTO for removing product images
 */
@Data
public class RemoveProductImagesRequest {
    
    @NotNull(message = "Image URLs list is required")
    @NotEmpty(message = "At least one image URL is required")
    private List<String> imageUrls;
}
