package com.importer_ecommerce.importEcommerce.auth.service;

import com.importer_ecommerce.importEcommerce.auth.dto.response.LoginResponse;
import com.importer_ecommerce.importEcommerce.auth.dto.response.OtpResponse;
import com.importer_ecommerce.importEcommerce.auth.entity.RefreshToken;
import com.importer_ecommerce.importEcommerce.auth.mapper.AuthMapper;
import com.importer_ecommerce.importEcommerce.common.util.CookieUtil;
import com.importer_ecommerce.importEcommerce.email.service.EmailService;
import com.importer_ecommerce.importEcommerce.user.entity.User;
import com.importer_ecommerce.importEcommerce.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Authentication service for handling login, OTP, and token management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final OtpService otpService;
    private final EmailService emailService;
    private final AuthMapper authMapper;
    private final CookieUtil cookieUtil;
    
    /**
     * Send OTP to user email
     */
    @Transactional
    public OtpResponse sendOtp(String email, String deviceId) {
        // Find user by email
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        
        // Check if user is admin
        if (!user.isAdmin()) {
            throw new IllegalArgumentException("Access denied. Admin privileges required.");
        }
        
        // Check if user is active
        if (!user.isActive()) {
            throw new IllegalArgumentException("Account is inactive. Please contact support.");
        }
        
        // Generate and store OTP
        String otp = otpService.generateAndStoreOtp(email, deviceId);
        
        // Send OTP email asynchronously
        String recipientName = user.getName() != null ? user.getName() : "Admin";
        emailService.sendOtpEmailAsync(email, recipientName, otp);
        
        log.info("OTP sent to admin email: {}", email);
        
        LocalDateTime expiresAt = otpService.getOtpExpiryTime(email, deviceId);
        return authMapper.toOtpResponse("OTP sent successfully to your email", expiresAt);
    }
    
    /**
     * Verify OTP and login user
     */
    @Transactional
    public LoginResponse verifyOtp(String email, String otp, String deviceId, HttpServletResponse response) {
        // Find user by email
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        
        // Check if user is admin
        if (!user.isAdmin()) {
            throw new IllegalArgumentException("Access denied. Admin privileges required.");
        }
        
        // Check if user is active
        if (!user.isActive()) {
            throw new IllegalArgumentException("Account is inactive. Please contact support.");
        }
        
        // Verify OTP
        if (!otpService.verifyOtp(email, deviceId, otp)) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }
        
        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        
        // Create refresh token entity
        RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(user, deviceId);
        
        // Set cookies
        Duration accessTokenDuration = Duration.ofMinutes(15); // 15 minutes
        Duration refreshTokenDuration = Duration.ofDays(7); // 7 days
        
        cookieUtil.addCookiesToResponse(response,
            cookieUtil.createAccessTokenCookie(accessToken, accessTokenDuration),
            cookieUtil.createRefreshTokenCookie(refreshTokenEntity.getToken(), refreshTokenDuration)
        );
        
        log.info("Admin logged in successfully: {}", email);
        
        LocalDateTime expiresAt = jwtService.getAccessTokenExpirationTime();
        return authMapper.toLoginResponse(accessToken, refreshTokenEntity.getToken(), user, expiresAt);
    }
    
    /**
     * Get current OTP for testing (DEV ONLY)
     */
    public String getCurrentOtpForTesting(String email, String deviceId) {
        // This is a test method - in production, remove this
        return otpService.getCurrentOtpForTesting(email, deviceId);
    }
    
    /**
     * Refresh access token
     */
    @Transactional
    public LoginResponse refreshToken(String refreshToken, String deviceId, HttpServletResponse response) {
        // Validate refresh token
        if (!refreshTokenService.validateRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }
        
        // Get user from refresh token
        User user = refreshTokenService.getUserFromToken(refreshToken)
            .orElseThrow(() -> new IllegalArgumentException("User not found for refresh token"));
        
        // Check if user is active
        if (!user.isActive()) {
            throw new IllegalArgumentException("Account is inactive. Please contact support.");
        }
        
        // Rotate refresh token (create new one and delete old one)
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken, deviceId);
        
        // Generate new access token
        String newAccessToken = jwtService.generateAccessToken(user);
        
        // Set cookies
        Duration accessTokenDuration = Duration.ofMinutes(15); // 15 minutes
        Duration refreshTokenDuration = Duration.ofDays(7); // 7 days
        
        cookieUtil.addCookiesToResponse(response,
            cookieUtil.createAccessTokenCookie(newAccessToken, accessTokenDuration),
            cookieUtil.createRefreshTokenCookie(newRefreshToken.getToken(), refreshTokenDuration)
        );
        
        log.info("Token refreshed successfully for user: {}", user.getEmail());
        
        LocalDateTime expiresAt = jwtService.getAccessTokenExpirationTime();
        return authMapper.toLoginResponse(newAccessToken, newRefreshToken.getToken(), user, expiresAt);
    }
    
    /**
     * Logout user (invalidate refresh token)
     */
    @Transactional
    public void logout(String refreshToken, String deviceId, HttpServletResponse response) {
        // Delete refresh token
        refreshTokenService.deleteByToken(refreshToken);
        
        // Clear cookies
        cookieUtil.addCookiesToResponse(response,
            cookieUtil.clearAccessTokenCookie(),
            cookieUtil.clearRefreshTokenCookie()
        );
        
        log.info("User logged out successfully");
    }
    
    /**
     * Logout user from all devices
     */
    @Transactional
    public void logoutAllDevices(String refreshToken, HttpServletResponse response) {
        // Get user from refresh token
        User user = refreshTokenService.getUserFromToken(refreshToken)
            .orElseThrow(() -> new IllegalArgumentException("User not found for refresh token"));
        
        // Delete all refresh tokens for user
        refreshTokenService.deleteAllByUser(user);
        
        // Clear cookies
        cookieUtil.addCookiesToResponse(response,
            cookieUtil.clearAccessTokenCookie(),
            cookieUtil.clearRefreshTokenCookie()
        );
        
        log.info("User logged out from all devices: {}", user.getEmail());
    }
    
    /**
     * Get user profile
     */
    public User getUserProfile(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }
    
    /**
     * Check if OTP exists for email and device
     */
    public boolean hasOtp(String email, String deviceId) {
        return otpService.hasOtp(email, deviceId);
    }
    
    /**
     * Remove OTP for email and device
     */
    public void removeOtp(String email, String deviceId) {
        otpService.removeOtp(email, deviceId);
    }
}
