package com.importer_ecommerce.importEcommerce.auth.service;

import com.importer_ecommerce.importEcommerce.auth.entity.User;
import com.importer_ecommerce.importEcommerce.auth.entity.UserRole;
import com.importer_ecommerce.importEcommerce.auth.entity.UserStatus;
import com.importer_ecommerce.importEcommerce.auth.repository.UserRepository;
import com.importer_ecommerce.importEcommerce.common.exception.BusinessException;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.common.constants.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
    
    public UserDetails loadUserByMobileNumber(String mobileNumber) throws UsernameNotFoundException {
        return userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with mobile number: " + mobileNumber));
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
}
