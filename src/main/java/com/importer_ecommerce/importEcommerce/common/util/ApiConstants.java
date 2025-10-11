package com.importer_ecommerce.importEcommerce.common.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * API constants for centralized endpoint management
 */
public class ApiConstants {
    
    // Public endpoints (no authentication required)
    public static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/v1/auth/send-otp",
        "/v1/auth/verify-otp",
        "/v1/auth/refresh",
        "/v1/health",
        "/v1/actuator/**",
        "/error"
    );
    
    // Admin endpoints (require ADMIN role)
    public static final List<String> ADMIN_ENDPOINTS = Arrays.asList(
        "/v1/admin/**"
    );
    
    // User endpoints (require USER role)
    public static final List<String> USER_ENDPOINTS = Arrays.asList(
        "/v1/user/**"
    );
    
    // Common endpoints (require any authenticated user)
    public static final List<String> AUTHENTICATED_ENDPOINTS = Arrays.asList(
        "/v1/auth/logout",
        "/v1/auth/profile"
    );
    
    /**
     * Check if endpoint is public
     */
    public static boolean isPublicEndpoint(String endpoint) {
        return PUBLIC_ENDPOINTS.stream()
            .anyMatch(pattern -> {
                if (pattern.contains("**")) {
                    // Convert ** to .* for regex matching
                    String regex = pattern.replace("**", ".*");
                    return endpoint.matches(regex);
                } else {
                    // Exact match for specific endpoints
                    return endpoint.equals(pattern);
                }
            });
    }
    
    /**
     * Check if endpoint requires admin role
     */
    public static boolean isAdminEndpoint(String endpoint) {
        return ADMIN_ENDPOINTS.stream()
            .anyMatch(pattern -> endpoint.matches(pattern.replace("**", ".*")));
    }
    
    /**
     * Check if endpoint requires user role
     */
    public static boolean isUserEndpoint(String endpoint) {
        return USER_ENDPOINTS.stream()
            .anyMatch(pattern -> endpoint.matches(pattern.replace("**", ".*")));
    }
    
    /**
     * Check if endpoint requires authentication
     */
    public static boolean isAuthenticatedEndpoint(String endpoint) {
        return AUTHENTICATED_ENDPOINTS.stream()
            .anyMatch(pattern -> endpoint.matches(pattern.replace("**", ".*")));
    }
    
    /**
     * Get current request
     */
    public static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
    
    /**
     * Get client IP address
     */
    public static String getClientIpAddress() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return "unknown";
        }
        
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
