package com.importer_ecommerce.importEcommerce.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Utility class for handling HTTP cookies
 */
@Component
public class CookieUtil {
    
    @Value("${app.cookie.domain:localhost}")
    private String cookieDomain;
    
    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;
    
    @Value("${app.cookie.same-site:Strict}")
    private String cookieSameSite;
    
    @Value("${app.cookie.access-token-max-age-days:7}")
    private int accessTokenMaxAgeDays;
    
    @Value("${app.cookie.refresh-token-max-age-days:7}")
    private int refreshTokenMaxAgeDays;
    
    /**
     * Create a cookie with default settings
     */
    public Cookie createCookie(String name, String value, Duration maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setMaxAge((int) maxAge.getSeconds());
        cookie.setPath("/");
        
        // Set domain only if not localhost
        if (!"localhost".equals(cookieDomain)) {
            cookie.setDomain(cookieDomain);
        }
        
        return cookie;
    }
    
    /**
     * Create access token cookie with configured expiration
     */
    public Cookie createAccessTokenCookie(String token) {
        return createCookie("access_token", token, Duration.ofDays(accessTokenMaxAgeDays));
    }
    
    /**
     * Create refresh token cookie with configured expiration
     */
    public Cookie createRefreshTokenCookie(String token) {
        return createCookie("refresh_token", token, Duration.ofDays(refreshTokenMaxAgeDays));
    }
    
    /**
     * Create access token cookie with custom expiration
     */
    public Cookie createAccessTokenCookie(String token, Duration maxAge) {
        return createCookie("access_token", token, maxAge);
    }
    
    /**
     * Create refresh token cookie with custom expiration
     */
    public Cookie createRefreshTokenCookie(String token, Duration maxAge) {
        return createCookie("refresh_token", token, maxAge);
    }
    
    /**
     * Add cookie to response
     */
    public void addCookieToResponse(HttpServletResponse response, Cookie cookie) {
        response.addCookie(cookie);
    }
    
    /**
     * Clear cookie by setting max age to 0
     */
    public Cookie clearCookie(String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        
        if (!"localhost".equals(cookieDomain)) {
            cookie.setDomain(cookieDomain);
        }
        
        return cookie;
    }
    
    /**
     * Clear access token cookie
     */
    public Cookie clearAccessTokenCookie() {
        return clearCookie("access_token");
    }
    
    /**
     * Clear refresh token cookie
     */
    public Cookie clearRefreshTokenCookie() {
        return clearCookie("refresh_token");
    }
    
    /**
     * Add multiple cookies to response
     */
    public void addCookiesToResponse(HttpServletResponse response, Cookie... cookies) {
        for (Cookie cookie : cookies) {
            response.addCookie(cookie);
        }
    }
}
