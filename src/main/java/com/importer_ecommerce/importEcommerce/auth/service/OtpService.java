package com.importer_ecommerce.importEcommerce.auth.service;

import com.importer_ecommerce.importEcommerce.auth.entity.Otp;
import com.importer_ecommerce.importEcommerce.auth.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

/**
 * OTP service for managing one-time passwords
 * Uses database storage for persistence and reliability
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {
    
    private final OtpRepository otpRepository;
    private final Random random = new Random();
    
    // OTP configuration
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    
    /**
     * Generate and store OTP for email and device
     */
    @Transactional
    public String generateAndStoreOtp(String email, String deviceId) {
        try {
            // Delete any existing OTP for this email and device using efficient query
            otpRepository.deleteByEmailAndDeviceId(email, deviceId);
            
            // Generate new OTP
            String otpCode = generateOtp();
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
            
            // Create and save OTP entity
            Otp otp = Otp.builder()
                .email(email)
                .deviceId(deviceId)
                .otpCode(otpCode)
                .expiresAt(expiresAt)
                .isUsed(false)
                .attempts(0)
                .build();
            
            otpRepository.save(otp);
            
            log.info("OTP generated and stored for email: {} and device: {}", email, deviceId);
            
            return otpCode;
            
        } catch (Exception e) {
            log.error("Failed to generate OTP for email: {} and device: {}", email, deviceId, e);
            throw new RuntimeException("Failed to generate OTP. Please try again.", e);
        }
    }
    
    /**
     * Verify OTP for email and device
     */
    @Transactional
    public boolean verifyOtp(String email, String deviceId, String otp) {
        LocalDateTime now = LocalDateTime.now();
        
        // Find active OTP
        Optional<Otp> otpOptional = otpRepository.findActiveOtpByEmailAndDeviceId(email, deviceId, now);
        
        if (otpOptional.isEmpty()) {
            log.warn("No active OTP found for email: {} and device: {}", email, deviceId);
            return false;
        }
        
        Otp otpEntity = otpOptional.get();
        
        // Check if max attempts exceeded
        if (otpEntity.isMaxAttemptsExceeded()) {
            log.warn("Max attempts exceeded for OTP verification. Email: {}, Device: {}", email, deviceId);
            otpEntity.markAsUsed();
            otpRepository.save(otpEntity);
            return false;
        }
        
        // Increment attempt count
        otpEntity.incrementAttempts();
        
        // Verify OTP code
        if (otpEntity.getOtpCode().equals(otp)) {
            // Mark as used and save
            otpEntity.markAsUsed();
            otpRepository.save(otpEntity);
            log.info("OTP verified successfully for email: {} and device: {}", email, deviceId);
            return true;
        } else {
            // Save updated attempt count
            otpRepository.save(otpEntity);
            log.warn("Invalid OTP provided for email: {} and device: {}. Attempt: {}", email, deviceId, otpEntity.getAttempts());
            return false;
        }
    }
    
    
    /**
     * Remove OTP for email and device
     */
    @Transactional
    public void removeOtp(String email, String deviceId) {
        otpRepository.findByEmailAndDeviceId(email, deviceId)
            .ifPresent(otpRepository::delete);
    }
    
    /**
     * Get OTP expiry time
     */
    public LocalDateTime getOtpExpiryTime(String email, String deviceId) {
        LocalDateTime now = LocalDateTime.now();
        return otpRepository.findActiveOtpByEmailAndDeviceId(email, deviceId, now)
            .map(Otp::getExpiresAt)
            .orElse(null);
    }
    
    /**
     * Generate random OTP
     */
    private String generateOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
    
    
    /**
     * Clean up expired OTPs (scheduled task)
     */
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    @Transactional
    public void cleanupExpiredOtps() {
        try {
            LocalDateTime now = LocalDateTime.now();
            int deletedCount = otpRepository.deleteExpiredOtps(now);
            if (deletedCount > 0) {
                log.info("Cleaned up {} expired OTPs", deletedCount);
            }
        } catch (Exception e) {
            log.error("Error during expired OTP cleanup", e);
        }
    }
    
    /**
     * Clean up old OTPs (scheduled task)
     */
    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    @Transactional
    public void cleanupOldOtps() {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusDays(7); // Keep OTPs for 7 days
            int deletedCount = otpRepository.deleteOldOtps(cutoffTime);
            if (deletedCount > 0) {
                log.info("Cleaned up {} old OTPs", deletedCount);
            }
        } catch (Exception e) {
            log.error("Error during old OTP cleanup", e);
        }
    }
    
    /**
     * Get total OTP count (for monitoring)
     */
    public long getTotalOtpCount() {
        return otpRepository.count();
    }
    
    /**
     * Clear all OTPs (for testing)
     */
    @Transactional
    public void clearAllOtps() {
        otpRepository.deleteAll();
        log.info("All OTPs cleared");
    }
    
}
