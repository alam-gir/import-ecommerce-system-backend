package com.importer_ecommerce.importEcommerce.modules.customers.controller;

import com.importer_ecommerce.importEcommerce.auth.service.JwtService;
import com.importer_ecommerce.importEcommerce.common.dto.response.ApiResponse;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.common.exception.UnauthorizedException;
import com.importer_ecommerce.importEcommerce.common.exception.ValidationException;
import com.importer_ecommerce.importEcommerce.common.util.CookieUtil;
import com.importer_ecommerce.importEcommerce.common.util.RequestUtil;
import com.importer_ecommerce.importEcommerce.modules.customers.dto.request.CustomerLoginRequest;
import com.importer_ecommerce.importEcommerce.modules.customers.dto.request.CustomerRegisterRequest;
import com.importer_ecommerce.importEcommerce.modules.customers.dto.request.ForgotPasswordRequest;
import com.importer_ecommerce.importEcommerce.modules.customers.dto.request.PhoneCheckRequest;
import com.importer_ecommerce.importEcommerce.modules.customers.dto.request.ResetPasswordRequest;
import com.importer_ecommerce.importEcommerce.modules.customers.dto.response.CustomerResponse;
import com.importer_ecommerce.importEcommerce.user.entity.User;
import com.importer_ecommerce.importEcommerce.modules.customers.mapper.CustomerMapper;
import com.importer_ecommerce.importEcommerce.modules.customers.service.CustomerService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/customers/auth")
@RequiredArgsConstructor
@Slf4j
public class CustomerAuthController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;
    private final JwtService jwtService;
    private final CookieUtil cookieUtil;

    /**
     * Check if customer exists by phone number
     */
    @PostMapping("/check-phone")
    public ResponseEntity<ApiResponse<Boolean>> checkCustomerExists(
            @Valid @RequestBody PhoneCheckRequest request) {
        
        log.info("Phone check request for: {}", request.getPhone());
        
        try {
            boolean exists = customerService.checkCustomerExists(request);
            String message = exists ? "Customer exists" : "Customer not found";
            
            return RequestUtil.success(message, exists);
            
        } catch (Exception e) {
            log.error("Error checking customer existence: {}", e.getMessage());
            return RequestUtil.error("Failed to check customer existence");
        }
    }

    /**
     * Login existing customer
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<CustomerResponse>> loginCustomer(
            @Valid @RequestBody CustomerLoginRequest request,
            HttpServletResponse response) {
        
        log.info("Login request for phone: {}", request.getPhone());
        
        try {
            User user = customerService.loginCustomer(request);
            
            // Generate tokens
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            
            // Set tokens in cookies
            Cookie accessTokenCookie = cookieUtil.createAccessTokenCookie(accessToken);
            Cookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(refreshToken);
            cookieUtil.addCookiesToResponse(response, accessTokenCookie, refreshTokenCookie);
            
            // Convert to response DTO
            CustomerResponse customerResponse = customerMapper.toResponse(user);
            
            log.info("Customer login successful for phone: {}", request.getPhone());
            return RequestUtil.success("Login successful", customerResponse);
            
        } catch (NotFoundException | UnauthorizedException e) {
            log.warn("Login failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage());
            return RequestUtil.error("Login failed");
        }
    }

    /**
     * Register new customer
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CustomerResponse>> registerCustomer(
            @Valid @RequestBody CustomerRegisterRequest request,
            HttpServletResponse response) {
        
        log.info("Registration request for phone: {}", request.getPhone());
        
        try {
            User user = customerService.registerCustomer(request);
            
            // Generate tokens
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            
            // Set tokens in cookies
            Cookie accessTokenCookie = cookieUtil.createAccessTokenCookie(accessToken);
            Cookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(refreshToken);
            cookieUtil.addCookiesToResponse(response, accessTokenCookie, refreshTokenCookie);
            
            // Convert to response DTO
            CustomerResponse customerResponse = customerMapper.toResponse(user);
            
            log.info("Customer registration successful for phone: {}", request.getPhone());
            return RequestUtil.success("Registration successful", customerResponse);
            
        } catch (ValidationException e) {
            log.warn("Registration failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error during registration: {}", e.getMessage());
            return RequestUtil.error("Registration failed");
        }
    }

    /**
     * Send forgot password email
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> sendForgotPasswordEmail(
            @Valid @RequestBody ForgotPasswordRequest request) {
        
        log.info("Forgot password request for email: {}", request.getEmail());
        
        try {
            customerService.sendForgotPasswordEmail(request);
            
            log.info("Forgot password email sent successfully to: {}", request.getEmail());
            return RequestUtil.success("Password reset email sent successfully", "Check your email for reset instructions");
            
        } catch (NotFoundException e) {
            log.warn("Forgot password failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error sending forgot password email: {}", e.getMessage());
            return RequestUtil.error("Failed to send password reset email");
        }
    }

    /**
     * Reset password with token
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        
        log.info("Reset password request");
        
        try {
            customerService.resetPassword(request);
            
            log.info("Password reset successfully");
            return RequestUtil.success("Password reset successfully", "You can now login with your new password");
            
        } catch (ValidationException e) {
            log.warn("Password reset failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error resetting password: {}", e.getMessage());
            return RequestUtil.error("Failed to reset password");
        }
    }
}
