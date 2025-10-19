package com.importer_ecommerce.importEcommerce.modules.customers.dto.request;

import com.importer_ecommerce.importEcommerce.common.dto.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegisterRequest extends BaseRequest {

    @NotBlank(message = "Phone number is required")
    @Size(min = 6, max = 16, message = "Phone number must be between 6 and 16 characters")
    private String phone;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
