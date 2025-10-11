package com.importer_ecommerce.importEcommerce.auth.repository;

import com.importer_ecommerce.importEcommerce.auth.entity.RefreshToken;
import com.importer_ecommerce.importEcommerce.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for RefreshToken entity
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    
    /**
     * Find refresh token by token string
     */
    Optional<RefreshToken> findByToken(String token);
    
    /**
     * Find refresh token by user and device ID
     */
    Optional<RefreshToken> findByUserAndDeviceId(User user, String deviceId);
    
    /**
     * Find all refresh tokens for a user
     */
    List<RefreshToken> findByUser(User user);
    
    /**
     * Find all refresh tokens for a user and device
     */
    List<RefreshToken> findAllByUserAndDeviceId(User user, String deviceId);
    
    /**
     * Delete refresh token by token string
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.token = :token")
    void deleteByToken(@Param("token") String token);
    
    /**
     * Delete all refresh tokens for a user
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    void deleteByUser(@Param("user") User user);
    
    /**
     * Delete all refresh tokens for a user and device
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user AND rt.deviceId = :deviceId")
    void deleteByUserAndDeviceId(@Param("user") User user, @Param("deviceId") String deviceId);
    
    /**
     * Delete expired refresh tokens
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
    
    /**
     * Find expired refresh tokens
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.expiresAt < :now")
    List<RefreshToken> findExpiredTokens(@Param("now") LocalDateTime now);
}
