package com.importer_ecommerce.importEcommerce.user.entity;

import com.importer_ecommerce.importEcommerce.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "token", name = "uk_password_reset_tokens_token")
       })
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PasswordResetToken extends BaseEntity {

    @Column(name = "token", unique = true, nullable = false, length = 255)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used", nullable = false)
    @Builder.Default
    private Boolean used = false;

    /**
     * Check if token is expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    /**
     * Check if token is valid (not expired and not used)
     */
    public boolean isValid() {
        return !this.used && !isExpired();
    }
}
