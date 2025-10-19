package com.importer_ecommerce.importEcommerce.auth.security;

import com.importer_ecommerce.importEcommerce.user.entity.User;
import com.importer_ecommerce.importEcommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Custom user details service for Spring Security
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Try to find user by email first, then by phone
        User user = userRepository.findByEmail(identifier)
            .orElseGet(() -> userRepository.findByPhone(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + identifier)));
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail() != null ? user.getEmail() : user.getPhone()) // Use email if available, otherwise phone
            .password(user.getPassword() != null ? user.getPassword() : "") // Use actual password if available
            .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
            .accountExpired(false)
            .accountLocked(!user.isActive())
            .credentialsExpired(false)
            .disabled(!user.isActive())
            .build();
    }
}
