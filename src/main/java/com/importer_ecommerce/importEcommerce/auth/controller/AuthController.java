package com.importer_ecommerce.importEcommerce.auth.controller;

import com.importer_ecommerce.importEcommerce.auth.dto.request.OtpRequest;
import com.importer_ecommerce.importEcommerce.auth.dto.request.OtpVerifyRequest;
import com.importer_ecommerce.importEcommerce.auth.dto.request.RefreshTokenRequest;
import com.importer_ecommerce.importEcommerce.auth.dto.response.LoginResponse;
import com.importer_ecommerce.importEcommerce.auth.dto.response.OtpResponse;
import com.importer_ecommerce.importEcommerce.auth.dto.response.ProfileResponse;
import com.importer_ecommerce.importEcommerce.auth.mapper.AuthMapper;
import com.importer_ecommerce.importEcommerce.auth.service.AuthService;
import com.importer_ecommerce.importEcommerce.common.dto.response.ApiResponse;
import com.importer_ecommerce.importEcommerce.common.util.RequestUtil;
import com.importer_ecommerce.importEcommerce.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller for handling login, OTP, and token operations
 */
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    private final AuthMapper authMapper;
    
    /**
     * Send OTP to user email
     */
    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<OtpResponse>> sendOtp(@Valid @RequestBody OtpRequest request) {
        try {
            OtpResponse response = authService.sendOtp(request.getEmail(), request.getDeviceId());
            return RequestUtil.success("OTP sent successfully", response);
        } catch (IllegalArgumentException e) {
            return RequestUtil.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error sending OTP to: {}", request.getEmail(), e);
            return RequestUtil.internalError("Failed to send OTP. Please try again.");
        }
    }
    
    /**
     * Verify OTP and login user
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request,
                                                             HttpServletResponse response) {
        try {
            LoginResponse loginResponse = authService.verifyOtp(request.getEmail(), request.getOtp(), request.getDeviceId(), response);
            return RequestUtil.success("Login successful", loginResponse);
        } catch (IllegalArgumentException e) {
            return RequestUtil.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error verifying OTP for: {}", request.getEmail(), e);
            return RequestUtil.internalError("Failed to verify OTP. Please try again.");
        }
    }
    
    /**
     * Refresh access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request,
                                                                 HttpServletResponse response) {
        try {
            LoginResponse loginResponse = authService.refreshToken(request.getRefreshToken(), request.getDeviceId(), response);
            return RequestUtil.success("Token refreshed successfully", loginResponse);
        } catch (IllegalArgumentException e) {
            return RequestUtil.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error refreshing token", e);
            return RequestUtil.internalError("Failed to refresh token. Please try again.");
        }
    }
    
    /**
     * Logout user
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@Valid @RequestBody RefreshTokenRequest request,
                                                      HttpServletResponse response) {
        try {
            authService.logout(request.getRefreshToken(), request.getDeviceId(), response);
            return RequestUtil.success("Logout successful", "User logged out successfully");
        } catch (IllegalArgumentException e) {
            return RequestUtil.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error during logout", e);
            return RequestUtil.internalError("Failed to logout. Please try again.");
        }
    }
    
    /**
     * Logout user from all devices
     */
    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse<String>> logoutAllDevices(@Valid @RequestBody RefreshTokenRequest request,
                                                               HttpServletResponse response) {
        try {
            authService.logoutAllDevices(request.getRefreshToken(), response);
            return RequestUtil.success("Logout from all devices successful", "User logged out from all devices");
        } catch (IllegalArgumentException e) {
            return RequestUtil.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error during logout from all devices", e);
            return RequestUtil.internalError("Failed to logout from all devices. Please try again.");
        }
    }
    
    /**
     * Get user profile
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = authService.getUserProfile(email);
            ProfileResponse profile = authMapper.toProfileResponse(user);
            return RequestUtil.success("Profile retrieved successfully", profile);
        } catch (IllegalArgumentException e) {
            return RequestUtil.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving profile", e);
            return RequestUtil.internalError("Failed to retrieve profile. Please try again.");
        }
    }
    
    /**
     * Check if OTP exists for email and device
     */
    @GetMapping("/otp-status")
    public ResponseEntity<ApiResponse<Boolean>> checkOtpStatus(@RequestParam String email,
                                                               @RequestParam String deviceId) {
        try {
            boolean hasOtp = authService.hasOtp(email, deviceId);
            return RequestUtil.success("OTP status retrieved successfully", hasOtp);
        } catch (Exception e) {
            log.error("Error checking OTP status for: {}", email, e);
            return RequestUtil.internalError("Failed to check OTP status. Please try again.");
        }
    }
    
    /**
     * Get current OTP for testing (DEV ONLY)
     */
    @GetMapping("/test-otp")
    public ResponseEntity<ApiResponse<String>> getTestOtp(@RequestParam String email, @RequestParam String deviceId) {
        try {
            // This is a test endpoint - in production, remove this
            String otp = authService.getCurrentOtpForTesting(email, deviceId);
            return RequestUtil.success("Current OTP retrieved", otp);
        } catch (IllegalArgumentException e) {
            return RequestUtil.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving test OTP", e);
            return RequestUtil.internalError("Failed to retrieve OTP. Please try again.");
        }
    }
}
