package com.importer_ecommerce.importEcommerce.auth.controller;

import com.importer_ecommerce.importEcommerce.auth.dto.*;
import com.importer_ecommerce.importEcommerce.auth.entity.CustomerProfile;
import com.importer_ecommerce.importEcommerce.auth.service.CustomerService;
import com.importer_ecommerce.importEcommerce.common.utils.ApiResponse;
import com.importer_ecommerce.importEcommerce.auth.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {
    
    private final CustomerService customerService;
    private final JwtUtils jwtUtils;
    
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<LoginResponse>> signup(@Valid @RequestBody CustomerSignupRequest request) {
        log.info("Customer signup request for phone: {}", request.getPhoneNumber());
        LoginResponse response = customerService.customerSignup(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Customer registered successfully"));
    }
    
    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> getProfile(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        Long userId = jwtUtils.extractUserId(token);
        
        if (userId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid token", "TOKEN_ERROR", request.getRequestURI()));
        }
        
        CustomerProfileResponse profile = customerService.getProfileResponse(userId);
        return ResponseEntity.ok(ApiResponse.success(profile, "Profile retrieved successfully"));
    }
    
    @PutMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updateProfile(@Valid @RequestBody ProfileUpdateRequest request, HttpServletRequest httpRequest) {
        String token = extractTokenFromRequest(httpRequest);
        Long userId = jwtUtils.extractUserId(token);
        
        if (userId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid token", "TOKEN_ERROR", httpRequest.getRequestURI()));
        }
        
        CustomerProfile profile = customerService.updateProfile(userId, request);
        CustomerProfileResponse response = customerService.convertToProfileResponse(profile);
        return ResponseEntity.ok(ApiResponse.success(response, "Profile updated successfully"));
    }
    
    @PutMapping("/email")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updateEmail(@Valid @RequestBody EmailUpdateRequest request, HttpServletRequest httpRequest) {
        String token = extractTokenFromRequest(httpRequest);
        Long userId = jwtUtils.extractUserId(token);
        
        if (userId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid token", "TOKEN_ERROR", httpRequest.getRequestURI()));
        }
        
        CustomerProfileResponse profile = customerService.updateEmail(userId, request);
        return ResponseEntity.ok(ApiResponse.success(profile, "Email updated successfully. Verification email sent."));
    }
    
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request, HttpServletRequest httpRequest) {
        String token = extractTokenFromRequest(httpRequest);
        Long userId = jwtUtils.extractUserId(token);
        
        if (userId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid token", "TOKEN_ERROR", httpRequest.getRequestURI()));
        }
        
        customerService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        customerService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset OTP sent to your email"));
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        customerService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successfully"));
    }
    
    @PostMapping("/send-email-verification")
    public ResponseEntity<ApiResponse<Void>> sendEmailVerification(@RequestParam String email) {
        try {
            customerService.sendEmailVerification(email);
            return ResponseEntity.ok(ApiResponse.success(null, "Email verification OTP sent"));
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("already verified")) {
                return ResponseEntity.badRequest().body(ApiResponse.error(
                    "Email is already verified: " + email, 
                    "EMAIL_ALREADY_VERIFIED", 
                    email
                ));
            }
            // Re-throw other IllegalStateException
            throw e;
        }
    }
    
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Boolean>> verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        try {
            boolean verified = customerService.verifyEmail(request);
            
            if (verified) {
                return ResponseEntity.ok(ApiResponse.success(true, "Email verified successfully"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(
                    "Email verification failed. Please check your email and OTP, or request a new verification code.", 
                    "INVALID_OTP", 
                    request.getEmail()
                ));
            }
        } catch (Exception e) {
            log.error("Error during email verification for email: {}", request.getEmail(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(
                "An error occurred during email verification. Please try again.", 
                "VERIFICATION_ERROR", 
                request.getEmail()
            ));
        }
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
