package com.importer_ecommerce.importEcommerce.auth.repository;

import com.importer_ecommerce.importEcommerce.auth.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for OTP entity
 */
@Repository
public interface OtpRepository extends JpaRepository<Otp, UUID> {
    
    /**
     * Find active OTP by email and device ID
     */
    @Query("SELECT o FROM Otp o WHERE o.email = :email AND o.deviceId = :deviceId AND o.isUsed = false AND o.expiresAt > :now ORDER BY o.createdAt DESC")
    Optional<Otp> findActiveOtpByEmailAndDeviceId(@Param("email") String email, 
                                                   @Param("deviceId") String deviceId, 
                                                   @Param("now") LocalDateTime now);
    
    /**
     * Find OTP by email and device ID (including expired/used)
     */
    Optional<Otp> findByEmailAndDeviceId(String email, String deviceId);
    
    /**
     * Delete expired OTPs
     */
    @Modifying
    @Query("DELETE FROM Otp o WHERE o.expiresAt < :now")
    int deleteExpiredOtps(@Param("now") LocalDateTime now);
    
    /**
     * Delete OTPs older than specified time
     */
    @Modifying
    @Query("DELETE FROM Otp o WHERE o.createdAt < :cutoffTime")
    int deleteOldOtps(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Delete OTP by email and device ID (more efficient)
     */
    @Modifying
    @Query("DELETE FROM Otp o WHERE o.email = :email AND o.deviceId = :deviceId")
    int deleteByEmailAndDeviceId(@Param("email") String email, @Param("deviceId") String deviceId);
    
    /**
     * Count active OTPs for email and device
     */
    @Query("SELECT COUNT(o) FROM Otp o WHERE o.email = :email AND o.deviceId = :deviceId AND o.isUsed = false AND o.expiresAt > :now")
    long countActiveOtpsByEmailAndDeviceId(@Param("email") String email, 
                                          @Param("deviceId") String deviceId, 
                                          @Param("now") LocalDateTime now);
}
