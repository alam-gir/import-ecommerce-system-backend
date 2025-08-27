package com.importer_ecommerce.importEcommerce.auth.service;

import com.importer_ecommerce.importEcommerce.auth.entity.CustomerProfile;
import com.importer_ecommerce.importEcommerce.auth.entity.User;
import com.importer_ecommerce.importEcommerce.auth.entity.UserRole;
import com.importer_ecommerce.importEcommerce.auth.entity.UserStatus;
import com.importer_ecommerce.importEcommerce.auth.repository.CustomerProfileRepository;
import com.importer_ecommerce.importEcommerce.auth.repository.UserRepository;
import com.importer_ecommerce.importEcommerce.common.constants.ErrorCodes;
import com.importer_ecommerce.importEcommerce.common.exception.BusinessException;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerProfileRepository customerProfileRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
    
    @Transactional
    public User createCustomer(String mobileNumber, String fullName, String password) {
        if (userRepository.existsByMobileNumber(mobileNumber)) {
            throw new BusinessException("Mobile number already registered", ErrorCodes.DUPLICATE_RESOURCE);
        }
        
        User user = new User();
        user.setUsername(mobileNumber); // Use mobile as username for customers
        user.setMobileNumber(mobileNumber);
        user.setFullName(fullName);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.CUSTOMER);
        user.setStatus(UserStatus.ACTIVE);
        
        User savedUser = userRepository.save(user);
        
        // Create customer profile
        CustomerProfile profile = new CustomerProfile();
        profile.setUser(savedUser);
        profile.setFullName(fullName);
        customerProfileRepository.save(profile);
        
        log.info("Customer created with mobile number: {}", mobileNumber);
        return savedUser;
    }
    
    @Transactional
    public User createStaff(String username, String email, String password, String fullName, UserRole role) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("Username already exists", ErrorCodes.DUPLICATE_RESOURCE);
        }
        if (email != null && userRepository.existsByEmail(email)) {
            throw new BusinessException("Email already registered", ErrorCodes.DUPLICATE_RESOURCE);
        }
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        
        User savedUser = userRepository.save(user);
        log.info("Staff created with username: {}", username);
        return savedUser;
    }
    
    @Transactional
    public void updateLastLogin(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
        });
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User", id.toString()));
    }
    
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User", username));
    }
    
    @Transactional
    public CustomerProfile updateCustomerProfile(Long userId, String fullName) {
        User user = getUserById(userId);
        if (user.getRole() != UserRole.CUSTOMER) {
            throw new BusinessException("Only customers can update profile", ErrorCodes.INVALID_OPERATION);
        }
        
        CustomerProfile profile = customerProfileRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Customer Profile", userId.toString()));
        
        profile.setFullName(fullName);
        
        CustomerProfile savedProfile = customerProfileRepository.save(profile);
        log.info("Customer profile updated for user: {}", userId);
        return savedProfile;
    }
    
    @Transactional
    public CustomerProfile saveCustomerProfile(CustomerProfile profile) {
        return customerProfileRepository.save(profile);
    }
    
    public CustomerProfile getCustomerProfile(Long userId) {
        User user = getUserById(userId);
        if (user.getRole() != UserRole.CUSTOMER) {
            throw new BusinessException("Only customers can access profile", ErrorCodes.INVALID_OPERATION);
        }
        
        return customerProfileRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Customer Profile", userId.toString()));
    }
    
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException("Current password is incorrect", ErrorCodes.INVALID_CREDENTIALS);
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed for user: {}", userId);
    }
    
    @Transactional
    public CustomerProfile updateCustomerEmail(Long userId, String email) {
        User user = getUserById(userId);
        if (user.getRole() != UserRole.CUSTOMER) {
            throw new BusinessException("Only customers can update email", ErrorCodes.INVALID_OPERATION);
        }
        
        CustomerProfile profile = customerProfileRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Customer Profile", userId.toString()));
        
        // Check if email is already used by another user
        if (email != null && !email.equals(profile.getEmail())) {
            if (customerProfileRepository.existsByEmail(email)) {
                throw new BusinessException("Email already registered by another user", ErrorCodes.DUPLICATE_RESOURCE);
            }
            profile.setEmailVerified(false); // Reset email verification when email changes
        }
        
        profile.setEmail(email);
        CustomerProfile savedProfile = customerProfileRepository.save(profile);
        log.info("Customer email updated for user: {}", userId);
        return savedProfile;
    }
    
    @Transactional
    public void markCustomerEmailAsVerified(String email) {
        CustomerProfile profile = customerProfileRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Customer Profile with email", email));
        
        profile.setEmailVerified(true);
        customerProfileRepository.save(profile);
        log.info("Email marked as verified for customer: {}", email);
    }
}
