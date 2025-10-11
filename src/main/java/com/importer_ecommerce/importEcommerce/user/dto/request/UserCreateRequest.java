package com.importer_ecommerce.importEcommerce.user.dto.request;

import com.importer_ecommerce.importEcommerce.common.dto.request.BaseRequest;
import com.importer_ecommerce.importEcommerce.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Request DTO for creating a new user
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequest extends BaseRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String phone;
    
    private String profileImage;
    
    @NotNull(message = "Role is required")
    private UserRole role;
    
    // Validation: Either email or phone must be provided
    @jakarta.validation.constraints.AssertTrue(message = "Either email or phone must be provided")
    public boolean isEmailOrPhoneProvided() {
        return (email != null && !email.trim().isEmpty()) || 
               (phone != null && !phone.trim().isEmpty());
    }
}
