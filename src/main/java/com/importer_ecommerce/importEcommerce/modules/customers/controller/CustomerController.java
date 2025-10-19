package com.importer_ecommerce.importEcommerce.modules.customers.controller;

import com.importer_ecommerce.importEcommerce.common.dto.response.ApiResponse;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.common.exception.ValidationException;
import com.importer_ecommerce.importEcommerce.common.util.RequestUtil;
import com.importer_ecommerce.importEcommerce.modules.customers.dto.request.UpdateProfileRequest;
import com.importer_ecommerce.importEcommerce.modules.customers.dto.response.CustomerResponse;
import com.importer_ecommerce.importEcommerce.user.entity.User;
import com.importer_ecommerce.importEcommerce.modules.customers.mapper.CustomerMapper;
import com.importer_ecommerce.importEcommerce.modules.customers.service.CustomerService;
import com.importer_ecommerce.importEcommerce.common.util.TokenExtractor;
import com.importer_ecommerce.importEcommerce.auth.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;
    private final JwtService jwtService;
    private final TokenExtractor tokenExtractor;

    /**
     * Extract customer ID from JWT token
     */
    private String getCustomerIdFromToken(HttpServletRequest request) {
        String token = tokenExtractor.extractAccessToken(request);
        if (token != null) {
            return jwtService.extractUserId(token).toString();
        }
        return null;
    }

    /**
     * Get customer profile
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerProfile(HttpServletRequest request) {
        log.info("Profile request for customer");
        
        try {
            // Extract customer ID from JWT token
            String customerIdStr = getCustomerIdFromToken(request);
            if (customerIdStr == null) {
                return RequestUtil.error("Unable to extract customer ID from token");
            }
            
                User user = customerService.getCustomerById(customerIdStr);
                CustomerResponse customerResponse = customerMapper.toResponse(user);
            
            return RequestUtil.success("Profile retrieved successfully", customerResponse);
            
        } catch (NotFoundException e) {
            log.warn("Profile not found: {}", e.getMessage());
            return RequestUtil.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving profile: {}", e.getMessage());
            return RequestUtil.error("Failed to retrieve profile");
        }
    }

    /**
     * Update customer profile
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomerProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Profile update request");
        
        try {
            // Extract customer ID from JWT token
            String customerIdStr = getCustomerIdFromToken(httpRequest);
            if (customerIdStr == null) {
                return RequestUtil.error("Unable to extract customer ID from token");
            }
            
                User user = customerService.updateCustomerProfile(customerIdStr, request);
                CustomerResponse customerResponse = customerMapper.toResponse(user);
            
            log.info("Profile updated successfully");
            return RequestUtil.success("Profile updated successfully", customerResponse);
            
        } catch (NotFoundException e) {
            log.warn("Profile update failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage());
        } catch (ValidationException e) {
            log.warn("Profile update failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating profile: {}", e.getMessage());
            return RequestUtil.error("Failed to update profile");
        }
    }

    /**
     * Update customer email
     */
    @PutMapping("/email")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomerEmail(
            @Valid @RequestBody UpdateProfileRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Email update request");
        
        try {
            // Extract customer ID from JWT token
            String customerIdStr = getCustomerIdFromToken(httpRequest);
            if (customerIdStr == null) {
                return RequestUtil.error("Unable to extract customer ID from token");
            }
            
                User user = customerService.updateCustomerEmail(customerIdStr, request.getEmail());
                CustomerResponse customerResponse = customerMapper.toResponse(user);
            
            log.info("Email updated successfully");
            return RequestUtil.success("Email updated successfully", customerResponse);
            
        } catch (NotFoundException e) {
            log.warn("Email update failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage());
        } catch (ValidationException e) {
            log.warn("Email update failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating email: {}", e.getMessage());
            return RequestUtil.error("Failed to update email");
        }
    }
}
