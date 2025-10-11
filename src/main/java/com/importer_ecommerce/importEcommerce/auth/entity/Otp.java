package com.importer_ecommerce.importEcommerce.auth.entity;

import com.importer_ecommerce.importEcommerce.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * OTP entity for storing one-time passwords in database
 * Supports automatic cleanup of expired OTPs
 */
@Entity
@Table(name = "otps",
       uniqueConstraints = @UniqueConstraint(columnNames = {"email", "device_id"}, name = "uk_otps_email_device"))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Otp extends BaseEntity {
    
    @Column(name = "email", nullable = false)
    private String email;
    
    @Column(name = "device_id", nullable = false)
    private String deviceId;
    
    @Column(name = "otp_code", nullable = false, length = 10)
    private String otpCode;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "is_used", nullable = false)
    @Builder.Default
    private Boolean isUsed = false;
    
    @Column(name = "attempts", nullable = false)
    @Builder.Default
    private Integer attempts = 0;
    
    /**
     * Check if OTP is expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
    
    /**
     * Check if OTP is valid (not expired and not used)
     */
    public boolean isValid() {
        return !isExpired() && !isUsed;
    }
    
    /**
     * Mark OTP as used
     */
    public void markAsUsed() {
        this.isUsed = true;
    }
    
    /**
     * Increment attempt count
     */
    public void incrementAttempts() {
        this.attempts++;
    }
    
    /**
     * Check if max attempts exceeded (max 3 attempts)
     */
    public boolean isMaxAttemptsExceeded() {
        return this.attempts >= 3;
    }
}
