package com.importer_ecommerce.importEcommerce.modules.product.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * Request DTO for updating product category
 */
@Data
public class UpdateProductCategoryRequest {
    
    @NotNull(message = "Category ID is required")
    private UUID categoryId;
}
