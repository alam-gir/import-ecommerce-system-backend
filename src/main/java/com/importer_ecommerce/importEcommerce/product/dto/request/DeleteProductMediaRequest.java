package com.importer_ecommerce.importEcommerce.product.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class DeleteProductMediaRequest {
    
    @NotEmpty(message = "Media URLs list cannot be empty")
    private List<String> mediaUrls;
}
