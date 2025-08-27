package com.importer_ecommerce.importEcommerce.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^01[3-9]\\d{8}$", message = "Phone number must be a valid Bangladeshi mobile number")
    private String phoneNumber;
    
    private String profilePicture;
    
    private LocalDateTime dateOfBirth;
}
