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
public class CustomerLoginRequest extends BaseRequest {

    @NotBlank(message = "Phone number is required")
    @Size(min = 6, max = 16, message = "Phone number must be between 6 and 16 characters")
    private String phone;

    @NotBlank(message = "Password is required")
    private String password;
}
