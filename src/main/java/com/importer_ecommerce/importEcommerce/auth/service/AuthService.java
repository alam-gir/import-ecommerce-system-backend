package com.importer_ecommerce.importEcommerce.auth.service;

import com.importer_ecommerce.importEcommerce.auth.dto.LoginRequest;
import com.importer_ecommerce.importEcommerce.auth.dto.LoginResponse;
import com.importer_ecommerce.importEcommerce.auth.entity.RefreshToken;
import com.importer_ecommerce.importEcommerce.auth.entity.User;
import com.importer_ecommerce.importEcommerce.auth.utils.JwtUtils;
import com.importer_ecommerce.importEcommerce.common.exception.BusinessException;
import com.importer_ecommerce.importEcommerce.common.constants.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;
    
    public LoginResponse login(LoginRequest request) {
        try {
            // Try to authenticate with username first (for staff)
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.getUserByUsername(userDetails.getUsername());
            
            // Update last login
            userService.updateLastLogin(user.getUsername());
            
            // Generate tokens
            String accessToken = jwtUtils.generateToken(userDetails);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            
            log.info("User logged in successfully: {}", user.getUsername());
            
            return new LoginResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtUtils.extractExpiration(accessToken).getTime() - System.currentTimeMillis(),
                user.getUsername(),
                user.getRole().name()
            );
            
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getUsername());
            throw new BusinessException("Invalid credentials", ErrorCodes.INVALID_CREDENTIALS);
        }
    }
    
    public LoginResponse refreshToken(String refreshToken) {
        RefreshToken token = refreshTokenService.findByToken(refreshToken)
            .orElseThrow(() -> new BusinessException("Refresh token not found", ErrorCodes.TOKEN_INVALID));
        
        // Verify expiration
        token = refreshTokenService.verifyExpiration(token);
        
        // Generate new access token
        User user = token.getUser();
        String newAccessToken = jwtUtils.generateToken(user);
        
        log.info("Token refreshed for user: {}", user.getUsername());
        
        return new LoginResponse(
            newAccessToken,
            refreshToken,
            "Bearer",
            jwtUtils.extractExpiration(newAccessToken).getTime() - System.currentTimeMillis(),
            user.getUsername(),
            user.getRole().name()
        );
    }
    
    public void logout(String refreshToken) {
        RefreshToken token = refreshTokenService.findByToken(refreshToken)
            .orElseThrow(() -> new BusinessException("Refresh token not found", ErrorCodes.TOKEN_INVALID));
        
        refreshTokenService.deleteByUser(token.getUser());
        log.info("User logged out: {}", token.getUser().getUsername());
    }
}
