package com.importer_ecommerce.importEcommerce.modules.product.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Request DTO for adding product description images
 */
@Data
public class AddProductDescriptionImagesRequest {
    
    @NotNull(message = "Description images list is required")
    @NotEmpty(message = "At least one description image is required")
    private List<MultipartFile> descriptionImages;
}
