package com.importer_ecommerce.importEcommerce.email.service;

import com.importer_ecommerce.importEcommerce.email.entity.EmailOtp;
import com.importer_ecommerce.importEcommerce.email.repository.EmailOtpRepository;
import com.importer_ecommerce.importEcommerce.auth.repository.CustomerProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailOtpService {
    
    private final EmailOtpRepository emailOtpRepository;
    private final EmailService emailService;
    private final CustomerProfileRepository customerProfileRepository;
    private final SecureRandom random = new SecureRandom();
    
    @Transactional
    public void generateAndSendOtp(String email, EmailOtp.OtpPurpose purpose) {
        // Check if email is already verified (only for EMAIL_VERIFICATION purpose)
        if (purpose == EmailOtp.OtpPurpose.EMAIL_VERIFICATION && isEmailAlreadyVerified(email)) {
            throw new IllegalStateException("Email is already verified: " + email);
        }
        
        // Delete any existing unused OTPs for this email and purpose
        emailOtpRepository.deleteByEmailAndPurpose(email, purpose);
        
        // Generate new OTP
        String otp = generateOtp();
        
        // Save OTP to database
        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setEmail(email);
        emailOtp.setOtp(otp);
        emailOtp.setPurpose(purpose);
        emailOtp.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        
        emailOtpRepository.save(emailOtp);
        
        // Send OTP email
        String purposeText = purpose.name();
        emailService.sendOtpEmail(email, otp, purposeText);
        
        log.info("OTP generated and sent for email: {} with purpose: {}", email, purpose);
    }
    
    private boolean isEmailAlreadyVerified(String email) {
        return customerProfileRepository.findByEmail(email)
                .map(profile -> profile.isEmailVerified())
                .orElse(false);
    }
    
    @Transactional
    public boolean validateOtp(String email, String otp, EmailOtp.OtpPurpose purpose) {
        log.info("Validating OTP for email: {}, OTP: {}, purpose: {}", email, otp, purpose);
        
        // First, find the OTP record for this specific email and purpose
        EmailOtp emailOtp = emailOtpRepository.findByEmailAndOtpAndPurposeAndUsedFalse(email, otp, purpose)
                .orElse(null);
        
        if (emailOtp == null) {
            log.warn("Invalid OTP attempt for email: {} with purpose: {} - No matching OTP found", email, purpose);
            return false;
        }
        
        log.info("Found OTP record: ID={}, ExpiresAt={}, Used={}", 
                emailOtp.getId(), emailOtp.getExpiresAt(), emailOtp.isUsed());
        
        // Check if OTP is expired
        if (emailOtp.isExpired()) {
            log.warn("Expired OTP attempt for email: {} with purpose: {} - OTP expired at {}", 
                    email, purpose, emailOtp.getExpiresAt());
            return false;
        }
        
        // Mark OTP as used
        emailOtp.setUsed(true);
        emailOtpRepository.save(emailOtp);
        
        log.info("OTP validated successfully for email: {} with purpose: {}", email, purpose);
        return true;
    }
    
    public void cleanupExpiredOtps() {
        // This method can be called by a scheduled task to clean up expired OTPs
        // For now, we'll rely on the isExpired() check during validation
        log.info("Cleanup of expired OTPs completed");
    }
    
    private String generateOtp() {
        // Generate 6-digit OTP
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
