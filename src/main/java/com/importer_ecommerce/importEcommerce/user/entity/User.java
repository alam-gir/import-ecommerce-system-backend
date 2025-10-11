package com.importer_ecommerce.importEcommerce.user.entity;

import com.importer_ecommerce.importEcommerce.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.UUID;

/**
 * User entity representing both admin and regular users
 * Distinguished by role field
 */
@Entity
@Table(name = "users", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "email", name = "uk_users_email"),
           @UniqueConstraint(columnNames = "phone", name = "uk_users_phone")
       })
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User extends AuditableEntity {
    
    @Column(name = "email", unique = true)
    private String email;
    
    @Column(name = "phone", unique = true)
    private String phone;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "profile_image")
    private String profileImage;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;
    
    /**
     * Check if user is admin
     */
    public boolean isAdmin() {
        return UserRole.ADMIN.equals(this.role);
    }
    
    /**
     * Check if user is active
     */
    public boolean isActive() {
        return UserStatus.ACTIVE.equals(this.status);
    }
    
    /**
     * Get display name (email or phone)
     */
    public String getDisplayName() {
        return email != null ? email : phone;
    }
}
