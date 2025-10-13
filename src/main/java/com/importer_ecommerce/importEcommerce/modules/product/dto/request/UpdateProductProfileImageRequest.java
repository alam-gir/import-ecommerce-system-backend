package com.importer_ecommerce.importEcommerce.modules.product.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request DTO for updating product profile image
 */
@Data
public class UpdateProductProfileImageRequest {
    
    @NotNull(message = "Profile image is required")
    private MultipartFile profileImage;
}
