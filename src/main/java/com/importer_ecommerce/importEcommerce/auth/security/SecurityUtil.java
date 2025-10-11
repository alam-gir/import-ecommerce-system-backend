package com.importer_ecommerce.importEcommerce.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import io.jsonwebtoken.Claims;

import java.util.Optional;
import java.util.UUID;

/**
 * Security utility class for handling authentication context
 */
public class SecurityUtil {
    
    /**
     * Get current authentication
     */
    public static Optional<Authentication> getCurrentAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }
    
    /**
     * Get current user ID from JWT token
     */
    public static Optional<UUID> getCurrentUserId() {
        return getCurrentAuthentication()
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getName)
            .map(email -> {
                try {
                    // For now, return a dummy UUID. In real implementation, 
                    // you would extract from JWT claims or load from database
                    return UUID.randomUUID();
                } catch (IllegalArgumentException e) {
                    return null;
                }
            });
    }
    
    /**
     * Get current user email from JWT token
     */
    public static Optional<String> getCurrentUserEmail() {
        return getCurrentAuthentication()
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getName);
    }
    
    /**
     * Get current user role from JWT token
     */
    public static Optional<String> getCurrentUserRole() {
        return getCurrentAuthentication()
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getAuthorities)
            .map(authorities -> authorities.iterator().next().getAuthority());
    }
    
    /**
     * Check if current user is admin
     */
    public static boolean isCurrentUserAdmin() {
        return getCurrentUserRole()
            .map(role -> "ADMIN".equals(role))
            .orElse(false);
    }
    
    /**
     * Check if current user is authenticated
     */
    public static boolean isAuthenticated() {
        return getCurrentAuthentication()
            .map(Authentication::isAuthenticated)
            .orElse(false);
    }
}
