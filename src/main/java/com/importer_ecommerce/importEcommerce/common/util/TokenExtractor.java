package com.importer_ecommerce.importEcommerce.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * Utility class for extracting tokens from requests
 */
@Component
public class TokenExtractor {
    
    /**
     * Extract access token from Authorization header or cookies
     */
    public String extractAccessToken(HttpServletRequest request) {
        // First try Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        // Then try access_token cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }
    
    /**
     * Extract refresh token from refresh_token header or cookies
     */
    public String extractRefreshToken(HttpServletRequest request) {
        // First try refresh_token header
        String refreshTokenHeader = request.getHeader("refresh_token");
        if (refreshTokenHeader != null && !refreshTokenHeader.trim().isEmpty()) {
            return refreshTokenHeader;
        }
        
        // Then try refresh_token cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }
}
