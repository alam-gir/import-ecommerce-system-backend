package com.importer_ecommerce.importEcommerce.auth.service;

import com.importer_ecommerce.importEcommerce.auth.entity.RefreshToken;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final OtpService otpService;
    private final EmailService emailService;
    private final CookieUtil cookieUtil;
    
    @Transactional
    public LocalDateTime sendOtp(String email, String deviceId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        
        if (!user.isAdmin()) {
            throw new IllegalArgumentException("Access denied. Admin privileges required.");
        }
        
        if (!user.isActive()) {
            throw new IllegalArgumentException("Account is inactive. Please contact support.");
        }
        
        String otp = otpService.generateAndStoreOtp(email, deviceId);
        emailService.sendOtpEmailAsync(email, user.getName(), otp);
        
        log.info("OTP sent to admin email: {}", email);
        
        return LocalDateTime.now().plusMinutes(5);
    }
    
    @Transactional
    public User verifyOtp(String email, String otp, String deviceId, HttpServletResponse response) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        
        if (!user.isAdmin()) {
            throw new IllegalArgumentException("Access denied. Admin privileges required.");
        }
        
        if (!user.isActive()) {
            throw new IllegalArgumentException("Account is inactive. Please contact support.");
        }
        
        boolean isValidOtp = otpService.verifyOtp(email, deviceId, otp);
        if (!isValidOtp) {
            throw new IllegalArgumentException("Invalid or expired OTP. Please try again.");
        }
        
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(user, deviceId);
        
        cookieUtil.addCookieToResponse(response, cookieUtil.createAccessTokenCookie(accessToken, Duration.ofMinutes(15)));
        cookieUtil.addCookieToResponse(response, cookieUtil.createRefreshTokenCookie(refreshTokenEntity.getToken(), Duration.ofDays(7)));
        
        log.info("Admin login successful: {} ({})", user.getName(), email);
        
        otpService.removeOtp(email, deviceId);
        
        return user;
    }
    
    public User refreshToken(String refreshToken, String deviceId, HttpServletResponse response) {
        RefreshToken refreshTokenEntity = refreshTokenService.findByToken(refreshToken)
            .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        
        if (refreshTokenEntity.isExpired()) {
            refreshTokenService.deleteByToken(refreshToken);
            throw new IllegalArgumentException("Refresh token expired. Please login again.");
        }
        
        User user = refreshTokenEntity.getUser();
        if (!user.isActive()) {
            throw new IllegalArgumentException("Account is inactive. Please contact support.");
        }
        
        String newAccessToken = jwtService.generateAccessToken(user);
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken, deviceId);
        
        cookieUtil.addCookieToResponse(response, cookieUtil.createAccessTokenCookie(newAccessToken, Duration.ofMinutes(15)));
        cookieUtil.addCookieToResponse(response, cookieUtil.createRefreshTokenCookie(newRefreshToken.getToken(), Duration.ofDays(7)));
        
        log.info("Token refreshed for user: {} ({})", user.getName(), user.getEmail());
        
        return user;
    }
    
    public void logout(String refreshToken, String deviceId, HttpServletResponse response) {
        refreshTokenService.deleteByToken(refreshToken);
        cookieUtil.addCookiesToResponse(response, 
            cookieUtil.clearAccessTokenCookie(), 
            cookieUtil.clearRefreshTokenCookie());
        
        log.info("User logged out from device: {}", deviceId);
    }
    
    public void logoutAllDevices(String refreshToken, HttpServletResponse response) {
        RefreshToken refreshTokenEntity = refreshTokenService.findByToken(refreshToken)
            .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        
        refreshTokenService.deleteAllByUser(refreshTokenEntity.getUser());
        cookieUtil.addCookiesToResponse(response, 
            cookieUtil.clearAccessTokenCookie(), 
            cookieUtil.clearRefreshTokenCookie());
        
        log.info("User logged out from all devices: {}", refreshTokenEntity.getUser().getEmail());
    }
    
    public User getUserProfile(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }
    
    public boolean hasOtp(String email, String deviceId) {
        return otpService.getOtpExpiryTime(email, deviceId) != null;
    }
}