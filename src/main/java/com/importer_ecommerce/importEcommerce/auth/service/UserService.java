package com.importer_ecommerce.importEcommerce.auth.service;

import com.importer_ecommerce.importEcommerce.auth.entity.CustomerProfile;
import com.importer_ecommerce.importEcommerce.auth.entity.User;
import com.importer_ecommerce.importEcommerce.auth.entity.UserRole;
import com.importer_ecommerce.importEcommerce.auth.entity.UserStatus;
import com.importer_ecommerce.importEcommerce.auth.repository.CustomerProfileRepository;
import com.importer_ecommerce.importEcommerce.auth.repository.UserRepository;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
    
    @Transactional
    public User createCustomer(String phoneNumber, String fullName, String password) {
        // Check if user already exists
        if (userRepository.existsByMobileNumber(phoneNumber)) {
            throw new BusinessException("User with phone number already exists: " + phoneNumber, "DUPLICATE_PHONE");
        }
        
        // Create user
        User user = new User();
        user.setUsername(phoneNumber);
        user.setMobileNumber(phoneNumber);
        user.setFullName(fullName);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.CUSTOMER);
        user.setStatus(UserStatus.ACTIVE);
        
        User savedUser = userRepository.save(user);
        
        // Create customer profile
        CustomerProfile profile = new CustomerProfile();
        profile.setUser(savedUser);
        customerProfileRepository.save(profile);
        
        log.info("Customer created successfully: {}", phoneNumber);
        return savedUser;
    }
    
    @Transactional
    public User createStaff(String email, String fullName, String password) {
        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("User with email already exists: " + email, "DUPLICATE_EMAIL");
        }
        
        // Create user
        User user = new User();
        user.setUsername(email);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.STAFF);
        user.setStatus(UserStatus.ACTIVE);
        
        User savedUser = userRepository.save(user);
        log.info("Staff created successfully: {}", email);
        return savedUser;
    }
    
    @Transactional
    public void updateLastLogin(UUID userId) {
        User user = getUserById(userId);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
    
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId.toString()));
    }
    
    public CustomerProfile getCustomerProfile(UUID userId) {
        User user = getUserById(userId);
        return customerProfileRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Customer Profile", userId.toString()));
    }
    
    @Transactional
    public CustomerProfile updateCustomerProfile(UUID userId, String fullName, String phoneNumber) {
        User user = getUserById(userId);
        
        // Check if phone number is already used by another user
        if (!phoneNumber.equals(user.getMobileNumber()) && userRepository.existsByMobileNumber(phoneNumber)) {
            throw new BusinessException("Phone number already registered by another user", "DUPLICATE_PHONE");
        }
        
        // Update user fields
        user.setFullName(fullName);
        user.setMobileNumber(phoneNumber);
        user.setUsername(phoneNumber); // Update username to match phone number for customers
        userRepository.save(user);
        
        CustomerProfile profile = getCustomerProfile(userId);
        return profile;
    }
    
    @Transactional
    public CustomerProfile saveCustomerProfile(CustomerProfile profile) {
        return customerProfileRepository.save(profile);
    }
    
    @Transactional
    public CustomerProfile updateCustomerEmail(UUID userId, String email) {
        CustomerProfile profile = getCustomerProfile(userId);
        profile.setEmail(email);
        profile.setEmailVerified(false);
        return customerProfileRepository.save(profile);
    }
    
    @Transactional
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException("Current password is incorrect", "INVALID_PASSWORD");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
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
