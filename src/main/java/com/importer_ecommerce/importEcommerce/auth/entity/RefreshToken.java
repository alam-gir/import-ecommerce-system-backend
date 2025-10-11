package com.importer_ecommerce.importEcommerce.auth.entity;

import com.importer_ecommerce.importEcommerce.common.entity.BaseEntity;
import com.importer_ecommerce.importEcommerce.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Refresh token entity for managing JWT refresh tokens
 * Supports multi-device login with device tracking
 */
@Entity
@Table(name = "refresh_tokens",
       uniqueConstraints = @UniqueConstraint(columnNames = "token", name = "uk_refresh_tokens_token"))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RefreshToken extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "token", nullable = false, unique = true, length = 1000)
    private String token;
    
    @Column(name = "device_id", nullable = false)
    private String deviceId;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    /**
     * Check if token is expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
    
    /**
     * Check if token is valid (not expired)
     */
    public boolean isValid() {
        return !isExpired();
    }
}
