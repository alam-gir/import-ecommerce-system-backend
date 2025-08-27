package com.importer_ecommerce.importEcommerce.auth.service;

import com.importer_ecommerce.importEcommerce.auth.entity.RefreshToken;
import com.importer_ecommerce.importEcommerce.auth.entity.User;
import com.importer_ecommerce.importEcommerce.auth.repository.RefreshTokenRepository;
import com.importer_ecommerce.importEcommerce.auth.utils.JwtUtils;
import com.importer_ecommerce.importEcommerce.common.exception.BusinessException;
import com.importer_ecommerce.importEcommerce.common.constants.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;
    
    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;
    
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Delete existing refresh token for user
        refreshTokenRepository.deleteByUserId(user.getId());
        
        // Create new refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setToken(jwtUtils.generateRefreshToken(user));
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000));
        
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        log.info("Refresh token created for user: {}", user.getUsername());
        return savedToken;
    }
    
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
    
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiresAt().compareTo(LocalDateTime.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new BusinessException("Refresh token was expired", ErrorCodes.TOKEN_EXPIRED);
        }
        return token;
    }
    
    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUserId(user.getId());
        log.info("Refresh tokens deleted for user: {}", user.getUsername());
    }
    
    @Transactional
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens();
        log.info("Expired refresh tokens cleaned up");
    }

    @Transactional
    public void deleteByUserId(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
        log.info("Refresh tokens deleted for user: {}", userId);
    }
}
