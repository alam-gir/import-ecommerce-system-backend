package com.importer_ecommerce.importEcommerce.auth.security;

import com.importer_ecommerce.importEcommerce.auth.service.JwtService;
import com.importer_ecommerce.importEcommerce.common.util.ApiConstants;
import com.importer_ecommerce.importEcommerce.common.util.TokenExtractor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT authentication filter for processing JWT tokens
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenExtractor tokenExtractor;
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        // Skip JWT processing for public endpoints
        String requestPath = request.getRequestURI();
        log.debug("Processing request: {}", requestPath);
        
        if (ApiConstants.isPublicEndpoint(requestPath)) {
            log.debug("Skipping JWT processing for public endpoint: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }
        
        log.debug("Processing JWT authentication for endpoint: {}", requestPath);
        
        String jwt = tokenExtractor.extractAccessToken(request);
        
        // If no access token found, continue without authentication
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            // Extract user email from JWT token
            String userEmail = jwtService.extractEmail(jwt);
            
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                
                // Validate JWT token
                if (jwtService.validateToken(jwt, userDetails)) {
                    // Extract role from JWT token
                    String role = jwtService.extractRole(jwt);
                    
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            log.error("JWT authentication failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }
        
        filterChain.doFilter(request, response);
    }
    
}
