package com.importer_ecommerce.importEcommerce.auth.mapper;

import com.importer_ecommerce.importEcommerce.auth.dto.response.LoginResponse;
import com.importer_ecommerce.importEcommerce.auth.dto.response.OtpResponse;
import com.importer_ecommerce.importEcommerce.auth.dto.response.ProfileResponse;
import com.importer_ecommerce.importEcommerce.auth.entity.RefreshToken;
import com.importer_ecommerce.importEcommerce.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper for authentication related DTOs
 */
@Component
public class AuthMapper {
    
    /**
     * Convert User entity to ProfileResponse
     */
    public ProfileResponse toProfileResponse(User user) {
        if (user == null) {
            return null;
        }
        
        return new ProfileResponse(
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
    
    /**
     * Convert User entity and tokens to LoginResponse
     */
    public LoginResponse toLoginResponse(String accessToken, String refreshToken, User user, LocalDateTime expiresAt) {
        ProfileResponse userProfile = toProfileResponse(user);
        
        return new LoginResponse(
            accessToken,
            refreshToken,
            userProfile,
            expiresAt
        );
    }
    
    /**
     * Create OtpResponse
     */
    public OtpResponse toOtpResponse(String message, LocalDateTime expiresAt) {
        return new OtpResponse(message, expiresAt);
    }
    
    /**
     * Convert request data to RefreshToken entity
     */
    public RefreshToken toRefreshTokenEntity(User user, String token, String deviceId, LocalDateTime expiresAt) {
        if (user == null || token == null || deviceId == null || expiresAt == null) {
            return null;
        }
        
        return RefreshToken.builder()
            .user(user)
            .token(token)
            .deviceId(deviceId)
            .expiresAt(expiresAt)
            .build();
    }
}
