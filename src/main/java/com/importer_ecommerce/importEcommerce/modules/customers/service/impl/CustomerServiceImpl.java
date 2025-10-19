package com.importer_ecommerce.importEcommerce.modules.customers.service.impl;

import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.common.exception.UnauthorizedException;
import com.importer_ecommerce.importEcommerce.common.exception.ValidationException;
import com.importer_ecommerce.importEcommerce.modules.customers.dto.request.CustomerLoginRequest;
import com.importer_ecommerce.importEcommerce.modules.customers.dto.request.CustomerRegisterRequest;
import com.importer_ecommerce.importEcommerce.modules.customers.dto.request.ForgotPasswordRequest;
import com.importer_ecommerce.importEcommerce.modules.customers.dto.request.PhoneCheckRequest;
import com.importer_ecommerce.importEcommerce.modules.customers.dto.request.ResetPasswordRequest;
import com.importer_ecommerce.importEcommerce.modules.customers.dto.request.UpdateProfileRequest;
import com.importer_ecommerce.importEcommerce.user.entity.User;
import com.importer_ecommerce.importEcommerce.user.entity.UserRole;
import com.importer_ecommerce.importEcommerce.user.entity.UserStatus;
import com.importer_ecommerce.importEcommerce.user.entity.PasswordResetToken;
import com.importer_ecommerce.importEcommerce.user.repository.UserRepository;
import com.importer_ecommerce.importEcommerce.user.repository.PasswordResetTokenRepository;
import com.importer_ecommerce.importEcommerce.modules.customers.service.CustomerService;
import com.importer_ecommerce.importEcommerce.email.dto.request.EmailRequest;
import com.importer_ecommerce.importEcommerce.email.service.EmailService;
import com.importer_ecommerce.importEcommerce.email.template.EmailTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailTemplate emailTemplate;

    @Override
    @Transactional(readOnly = true)
    public boolean checkCustomerExists(PhoneCheckRequest request) {
        log.info("Checking if customer exists with phone: {}", request.getPhone());

        boolean exists = userRepository.existsByPhone(request.getPhone());
        log.info("Customer exists: {}", exists);

        return exists;
    }

    @Override
    @Transactional(readOnly = true)
    public User loginCustomer(CustomerLoginRequest request) {
        log.info("Attempting login for customer with phone: {}", request.getPhone());

        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new NotFoundException("Customer not found with phone: " + request.getPhone()));

        // Check if this is a customer (USER role)
        if (user.getRole() != UserRole.USER) {
            throw new UnauthorizedException("Invalid customer account");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedException("Customer account is not active");
        }

        log.info("Customer login successful for phone: {}", request.getPhone());
        return user;
    }

    @Override
    @Transactional
    public User registerCustomer(CustomerRegisterRequest request) {
        log.info("Attempting to register new customer with phone: {}", request.getPhone());

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ValidationException("Customer already exists with phone: " + request.getPhone());
        }

        User user = User.builder()
                .phone(request.getPhone())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER) // Customer role
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Customer registered successfully with phone: {}", request.getPhone());

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User getCustomerById(String customerIdStr) {
        log.info("Getting customer by ID: {}", customerIdStr);

        UUID customerId = UUID.fromString(customerIdStr);
        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found with ID: " + customerIdStr));

        // Verify this is a customer (USER role)
        if (user.getRole() != UserRole.USER) {
            throw new NotFoundException("Customer not found with ID: " + customerIdStr);
        }

        return user;
    }

    @Override
    @Transactional
    public User updateCustomerProfile(String customerIdStr, UpdateProfileRequest request) {
        log.info("Updating customer profile for ID: {}", customerIdStr);

        User user = getCustomerById(customerIdStr);

        // Update fields if provided
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName().trim());
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            // Check if email is already taken by another user
            if (userRepository.existsByEmailExcludingId(request.getEmail().trim(), user.getId())) {
                throw new ValidationException("Email is already taken by another user");
            }
            user.setEmail(request.getEmail().trim());
        }

        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage().trim());
        }

        User updatedUser = userRepository.save(user);
        log.info("Customer profile updated successfully for ID: {}", customerIdStr);

        return updatedUser;
    }

    @Override
    @Transactional
    public User updateCustomerEmail(String customerIdStr, String email) {
        log.info("Updating customer email for ID: {}", customerIdStr);

        User user = getCustomerById(customerIdStr);

        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty");
        }

        // Check if email is already taken by another user
        if (userRepository.existsByEmailExcludingId(email.trim(), user.getId())) {
            throw new ValidationException("Email is already taken by another user");
        }

        user.setEmail(email.trim());
        User updatedUser = userRepository.save(user);

        log.info("Customer email updated successfully for ID: {}", customerIdStr);
        return updatedUser;
    }

    @Override
    @Transactional
    public void sendForgotPasswordEmail(ForgotPasswordRequest request) {
        log.info("Sending forgot password email to: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("No customer found with email: " + request.getEmail()));

        // Verify this is a customer (USER role)
        if (user.getRole() != UserRole.USER) {
            throw new NotFoundException("No customer found with email: " + request.getEmail());
        }

        // Delete any existing reset tokens for this user
        passwordResetTokenRepository.deleteByUserId(user.getId());

        // Generate reset token
        String resetToken = UUID.randomUUID().toString();

        // Create password reset token with 10 minutes expiration
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(resetToken)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();

        passwordResetTokenRepository.save(passwordResetToken);

        // Generate reset link using configured frontend URL
        String resetLink = frontendUrl + "/forgotPassword?token=" + resetToken;

        // Generate email content
        String emailContent = emailTemplate.generatePasswordResetEmail(user.getName(), resetLink);

        // Send email
        EmailRequest emailRequest = EmailRequest.builder()
                .to(request.getEmail())
                .subject("Password Reset Request - Taqreem Ecommerce")
                .content(emailContent)
                .recipientName(user.getName())
                .build();

        emailService.sendHtmlEmail(emailRequest);

        log.info("Forgot password email sent successfully to: {}", request.getEmail());
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        log.info("Resetting password with token");

        // Find the password reset token
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new ValidationException("Invalid or expired reset token"));

        // Validate token
        if (!passwordResetToken.isValid()) {
            if (passwordResetToken.isExpired()) {
                throw new ValidationException("Reset token has expired");
            } else if (passwordResetToken.getUsed()) {
                throw new ValidationException("Reset token has already been used");
            }
        }

        // Get the user
        User user = passwordResetToken.getUser();

        // Verify this is a customer (USER role)
        if (user.getRole() != UserRole.USER) {
            throw new ValidationException("Invalid reset token");
        }

        // Update user password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark token as used
        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);

        log.info("Password reset successfully for user: {}", user.getId());
    }
}
