package com.importer_ecommerce.importEcommerce.auth.controller;

import com.importer_ecommerce.importEcommerce.auth.dto.request.OtpRequest;
import com.importer_ecommerce.importEcommerce.auth.dto.request.OtpVerifyRequest;
import com.importer_ecommerce.importEcommerce.auth.dto.response.AuthUserResponse;
import com.importer_ecommerce.importEcommerce.auth.dto.response.OtpSendResponse;
import com.importer_ecommerce.importEcommerce.auth.mapper.AuthResponseMapper;
import com.importer_ecommerce.importEcommerce.auth.service.AuthService;
import com.importer_ecommerce.importEcommerce.common.dto.response.ApiResponse;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.common.exception.ValidationException;
import com.importer_ecommerce.importEcommerce.common.util.RequestUtil;
import com.importer_ecommerce.importEcommerce.common.util.TokenExtractor;
import com.importer_ecommerce.importEcommerce.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    private final AuthResponseMapper authResponseMapper;
    private final TokenExtractor tokenExtractor;
    
    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<OtpSendResponse>> sendOtp(@Valid @RequestBody OtpRequest request) {
        try {
            LocalDateTime expiresAt = authService.sendOtp(request.getEmail(), request.getDeviceId());
            OtpSendResponse response = authResponseMapper.toOtpSendResponse("OTP sent successfully", expiresAt);
            return RequestUtil.success("OTP sent successfully", response);
        } catch (ValidationException e) {
            return RequestUtil.error(e.getMessage(), e.getFieldErrors());
        } catch (NotFoundException e) {
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Error sending OTP to: {}", request.getEmail(), e);
            return RequestUtil.internalError("Failed to send OTP. Please try again.");
        }
    }
    
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthUserResponse>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request,
                                                             HttpServletResponse response) {
        try {
            User user = authService.verifyOtp(request.getEmail(), request.getOtp(), request.getDeviceId(), response);
            AuthUserResponse userResponse = authResponseMapper.toAuthUserResponse(user);
            return RequestUtil.success("Login successful", userResponse);
        } catch (ValidationException e) {
            return RequestUtil.error(e.getMessage(), e.getFieldErrors());
        } catch (NotFoundException e) {
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Error verifying OTP for: {}", request.getEmail(), e);
            return RequestUtil.internalError("Failed to verify OTP. Please try again.");
        }
    }
    
    @GetMapping("/refresh")
    public ResponseEntity<ApiResponse<Boolean>> refreshToken(@RequestParam String deviceId,
                                                             HttpServletRequest request,
                                                             HttpServletResponse response) {
        try {
            String refreshToken = tokenExtractor.extractRefreshToken(request);
            authService.refreshToken(refreshToken, deviceId, response);
            return RequestUtil.success("Token refreshed successfully", true);
        } catch (ValidationException e) {
            return RequestUtil.error(e.getMessage(), e.getFieldErrors());
        } catch (NotFoundException e) {
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Error refreshing token", e);
            return RequestUtil.internalError("Failed to refresh token. Please try again.");
        }
    }
    
    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestParam String deviceId,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) {
        try {
            String refreshToken = tokenExtractor.extractRefreshToken(request);
            authService.logout(refreshToken, deviceId, response);
            return RequestUtil.success("Logout successful", "User logged out successfully");
        } catch (ValidationException e) {
            return RequestUtil.error(e.getMessage(), e.getFieldErrors());
        } catch (NotFoundException e) {
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Error during logout", e);
            return RequestUtil.internalError("Failed to logout. Please try again.");
        }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<AuthUserResponse>> getProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = authService.getUserProfile(email);
            AuthUserResponse userResponse = authResponseMapper.toAuthUserResponse(user);
            return RequestUtil.success("Profile retrieved successfully", userResponse);
        } catch (NotFoundException e) {
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving profile", e);
            return RequestUtil.internalError("Failed to retrieve profile. Please try again.");
        }
    }
}