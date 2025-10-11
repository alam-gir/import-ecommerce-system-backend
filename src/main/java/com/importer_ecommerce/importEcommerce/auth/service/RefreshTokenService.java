package com.importer_ecommerce.importEcommerce.auth.service;

import com.importer_ecommerce.importEcommerce.auth.entity.RefreshToken;
import com.importer_ecommerce.importEcommerce.auth.repository.RefreshTokenRepository;
import com.importer_ecommerce.importEcommerce.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing refresh tokens
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    
    /**
     * Create refresh token for user and device
     */
    @Transactional
    public RefreshToken createRefreshToken(User user, String deviceId) {
        // Remove existing refresh token for this user and device
        refreshTokenRepository.deleteByUserAndDeviceId(user, deviceId);
        
        // Generate new refresh token
        String token = jwtService.generateRefreshToken(user);
        LocalDateTime expiresAt = jwtService.getRefreshTokenExpirationTime();
        
        RefreshToken refreshToken = RefreshToken.builder()
            .user(user)
            .token(token)
            .deviceId(deviceId)
            .expiresAt(expiresAt)
            .build();
        
        return refreshTokenRepository.save(refreshToken);
    }
    
    /**
     * Find refresh token by token string
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
    
    /**
     * Find refresh token by user and device ID
     */
    public Optional<RefreshToken> findByUserAndDeviceId(User user, String deviceId) {
        return refreshTokenRepository.findByUserAndDeviceId(user, deviceId);
    }
    
    /**
     * Find all refresh tokens for a user
     */
    public List<RefreshToken> findByUser(User user) {
        return refreshTokenRepository.findByUser(user);
    }
    
    /**
     * Validate refresh token
     */
    public boolean validateRefreshToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = findByToken(token);
        
        if (refreshTokenOpt.isEmpty()) {
            return false;
        }
        
        RefreshToken refreshToken = refreshTokenOpt.get();
        
        // Check if token is expired
        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            return false;
        }
        
        // Validate with JWT service
        return jwtService.validateToken(token, refreshToken.getUser());
    }
    
    /**
     * Delete refresh token by token string
     */
    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
    
    /**
     * Delete refresh token by user and device ID
     */
    @Transactional
    public void deleteByUserAndDeviceId(User user, String deviceId) {
        refreshTokenRepository.deleteByUserAndDeviceId(user, deviceId);
    }
    
    /**
     * Delete all refresh tokens for a user
     */
    @Transactional
    public void deleteAllByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
    
    /**
     * Delete expired refresh tokens
     */
    @Transactional
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
    
    /**
     * Rotate refresh token (create new one and delete old one)
     */
    @Transactional
    public RefreshToken rotateRefreshToken(String oldToken, String deviceId) {
        Optional<RefreshToken> oldRefreshTokenOpt = findByToken(oldToken);
        
        if (oldRefreshTokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        
        RefreshToken oldRefreshToken = oldRefreshTokenOpt.get();
        User user = oldRefreshToken.getUser();
        
        // Delete old token
        refreshTokenRepository.delete(oldRefreshToken);
        
        // Create new token
        return createRefreshToken(user, deviceId);
    }
    
    /**
     * Get user from refresh token
     */
    public Optional<User> getUserFromToken(String token) {
        return findByToken(token)
            .map(RefreshToken::getUser);
    }
    
    /**
     * Check if user has active refresh token for device
     */
    public boolean hasActiveTokenForDevice(User user, String deviceId) {
        Optional<RefreshToken> tokenOpt = findByUserAndDeviceId(user, deviceId);
        return tokenOpt.isPresent() && tokenOpt.get().isValid();
    }
    
    /**
     * Get active device count for user
     */
    public long getActiveDeviceCount(User user) {
        return refreshTokenRepository.findByUser(user)
            .stream()
            .filter(RefreshToken::isValid)
            .count();
    }
}
