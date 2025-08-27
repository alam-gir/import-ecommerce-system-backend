package com.importer_ecommerce.importEcommerce.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProfileUpdateRequest {
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    private String profilePicture;
    private LocalDateTime dateOfBirth;
}
