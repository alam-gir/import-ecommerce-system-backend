package com.importer_ecommerce.importEcommerce.auth.dto.request;

import com.importer_ecommerce.importEcommerce.common.dto.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Request DTO for refreshing access token
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest extends BaseRequest {
    
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
    
    @NotBlank(message = "Device ID is required")
    private String deviceId;
}
