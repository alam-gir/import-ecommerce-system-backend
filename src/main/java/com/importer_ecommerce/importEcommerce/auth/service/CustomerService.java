package com.importer_ecommerce.importEcommerce.auth.service;

import com.importer_ecommerce.importEcommerce.auth.dto.*;
import com.importer_ecommerce.importEcommerce.auth.entity.CustomerProfile;
import com.importer_ecommerce.importEcommerce.auth.entity.User;
import com.importer_ecommerce.importEcommerce.email.entity.EmailOtp;
import com.importer_ecommerce.importEcommerce.email.service.EmailOtpService;
import com.importer_ecommerce.importEcommerce.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    
    private final UserService userService;
    private final AuthService authService;
    private final EmailOtpService emailOtpService;
    private final EmailService emailService;
    
    @Transactional
    public LoginResponse customerSignup(CustomerSignupRequest request) {
        // Create customer user
        User customer = userService.createCustomer(
            request.getPhoneNumber(), 
            request.getFullName(), 
            request.getPassword()
        );
        
        // Note: Welcome email will be sent when customer adds email to profile
        
        // Auto-login: create login request and authenticate
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getPhoneNumber());
        loginRequest.setPassword(request.getPassword());
        
        return authService.login(loginRequest);
    }
    
    @Transactional
    public CustomerProfile updateProfile(UUID userId, ProfileUpdateRequest request) {
        CustomerProfile profile = userService.updateCustomerProfile(
            userId, 
            request.getFullName(),
            request.getPhoneNumber()
        );
        
        // Update additional profile fields
        if (request.getProfilePicture() != null) {
            profile.setProfilePicture(request.getProfilePicture());
        }
        if (request.getDateOfBirth() != null) {
            profile.setDateOfBirth(request.getDateOfBirth());
        }
        
        // Save the updated profile
        profile = userService.saveCustomerProfile(profile);
        
        // Send profile update confirmation email if email exists
        if (profile.getEmail() != null) {
            try {
                emailService.sendProfileUpdateEmail(profile.getEmail(), profile.getUser().getFullName(), "Profile Information");
            } catch (Exception e) {
                log.warn("Failed to send profile update email to: {}", profile.getEmail(), e);
            }
        }
        
        return profile;
    }
    
    public CustomerProfile getProfile(UUID userId) {
        return userService.getCustomerProfile(userId);
    }
    
    public CustomerProfileResponse getProfileResponse(UUID userId) {
        CustomerProfile profile = userService.getCustomerProfile(userId);
        return convertToProfileResponse(profile);
    }
    
    public CustomerProfileResponse convertToProfileResponse(CustomerProfile profile) {
        return CustomerProfileResponse.builder()
                .id(profile.getId())
                .fullName(profile.getUser().getFullName())
                .mobileNumber(profile.getUser().getMobileNumber())
                .email(profile.getEmail())
                .emailVerified(profile.isEmailVerified())
                .profilePicture(profile.getProfilePicture())
                .dateOfBirth(profile.getDateOfBirth())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
    
    @Transactional
    public CustomerProfileResponse updateEmail(UUID userId, EmailUpdateRequest request) {
        // Update email in customer profile
        CustomerProfile profile = userService.updateCustomerEmail(userId, request.getEmail());
        
        // Send verification email immediately
        try {
            emailOtpService.generateAndSendOtp(request.getEmail(), EmailOtp.OtpPurpose.EMAIL_VERIFICATION);
            log.info("Verification email sent to: {}", request.getEmail());
        } catch (Exception e) {
            log.warn("Failed to send verification email to: {}", request.getEmail(), e);
        }
        
        return convertToProfileResponse(profile);
    }
    
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        userService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
    }
    
    public void forgotPassword(ForgotPasswordRequest request) {
        // Generate and send OTP for password reset
        emailOtpService.generateAndSendOtp(request.getEmail(), EmailOtp.OtpPurpose.PASSWORD_RESET);
    }
    
    public void resetPassword(ResetPasswordRequest request) {
        // Validate OTP and reset password
        boolean isValidOtp = emailOtpService.validateOtp(
            request.getEmail(), 
            request.getOtp(), 
            EmailOtp.OtpPurpose.PASSWORD_RESET
        );
        
        if (!isValidOtp) {
            throw new RuntimeException("Invalid or expired OTP");
        }
        
        // Find user by email and update password
        // This would need to be implemented in UserService
        // For now, we'll throw an exception
        throw new UnsupportedOperationException("Password reset by email not yet implemented");
    }
    
    public void sendEmailVerification(String email) {
        try {
            // Generate and send OTP for email verification
            emailOtpService.generateAndSendOtp(email, EmailOtp.OtpPurpose.EMAIL_VERIFICATION);
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("already verified")) {
                throw new IllegalStateException("Email is already verified: " + email);
            }
            throw e;
        }
    }
    
    public boolean verifyEmail(EmailVerificationRequest request) {
        try {
            // Validate OTP for email verification
            boolean isValidOtp = emailOtpService.validateOtp(
                request.getEmail(), 
                request.getOtp(), 
                EmailOtp.OtpPurpose.EMAIL_VERIFICATION
            );
            
            if (isValidOtp) {
                // Mark email as verified in customer profile
                try {
                    userService.markCustomerEmailAsVerified(request.getEmail());
                    log.info("Email verified successfully: {}", request.getEmail());
                } catch (Exception e) {
                    log.error("Failed to mark email as verified: {}", request.getEmail(), e);
                    // Even if marking as verified fails, the OTP was valid
                    // So we return true but log the error
                }
                return true;
            } else {
                log.warn("Email verification failed for email: {} - Invalid or expired OTP", request.getEmail());
                return false;
            }
        } catch (Exception e) {
            log.error("Error during email verification for email: {}", request.getEmail(), e);
            return false;
        }
    }
}
