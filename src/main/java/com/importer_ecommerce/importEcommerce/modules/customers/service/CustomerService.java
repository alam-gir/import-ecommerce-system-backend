package com.importer_ecommerce.importEcommerce.modules.customers.service;

import com.importer_ecommerce.importEcommerce.modules.customers.dto.request.CustomerLoginRequest;
import com.importer_ecommerce.importEcommerce.modules.customers.dto.request.CustomerRegisterRequest;
import com.importer_ecommerce.importEcommerce.modules.customers.dto.request.ForgotPasswordRequest;
import com.importer_ecommerce.importEcommerce.modules.customers.dto.request.PhoneCheckRequest;
import com.importer_ecommerce.importEcommerce.modules.customers.dto.request.ResetPasswordRequest;
import com.importer_ecommerce.importEcommerce.modules.customers.dto.request.UpdateProfileRequest;
import com.importer_ecommerce.importEcommerce.user.entity.User;

public interface CustomerService {

    /**
     * Check if customer exists by phone number
     */
    boolean checkCustomerExists(PhoneCheckRequest request);

    /**
     * Login existing customer with phone and password
     */
    User loginCustomer(CustomerLoginRequest request);

    /**
     * Register new customer with phone, name and password
     */
    User registerCustomer(CustomerRegisterRequest request);

    /**
     * Get customer by ID
     */
    User getCustomerById(String customerIdStr);

    /**
     * Update customer profile
     */
    User updateCustomerProfile(String customerIdStr, UpdateProfileRequest request);

    /**
     * Update customer email
     */
    User updateCustomerEmail(String customerIdStr, String email);

    /**
     * Send forgot password email
     */
    void sendForgotPasswordEmail(ForgotPasswordRequest request);

    /**
     * Reset password with token
     */
    void resetPassword(ResetPasswordRequest request);
}
