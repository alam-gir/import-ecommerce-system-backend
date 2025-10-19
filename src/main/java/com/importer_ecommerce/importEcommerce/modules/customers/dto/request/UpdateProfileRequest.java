package com.importer_ecommerce.importEcommerce.modules.customers.dto.request;

import com.importer_ecommerce.importEcommerce.common.dto.request.BaseRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest extends BaseRequest {

    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 500, message = "Profile image URL must not exceed 500 characters")
    private String profileImage;
}
