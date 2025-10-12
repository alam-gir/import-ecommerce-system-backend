package com.importer_ecommerce.importEcommerce.auth.mapper;

import com.importer_ecommerce.importEcommerce.auth.dto.response.AuthUserResponse;
import com.importer_ecommerce.importEcommerce.auth.dto.response.OtpSendResponse;
import com.importer_ecommerce.importEcommerce.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuthResponseMapper {

    public OtpSendResponse toOtpSendResponse(String message, LocalDateTime expiresAt) {
        return new OtpSendResponse(message, expiresAt);
    }

    public AuthUserResponse toAuthUserResponse(User user) {
        return new AuthUserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhone(),
            user.getProfileImage(),
            user.getRole(),
            user.getStatus(),
            user.getCreatedAt()
        );
    }
}
