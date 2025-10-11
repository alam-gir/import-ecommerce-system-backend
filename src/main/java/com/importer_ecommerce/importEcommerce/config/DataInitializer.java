package com.importer_ecommerce.importEcommerce.config;

import com.importer_ecommerce.importEcommerce.user.entity.User;
import com.importer_ecommerce.importEcommerce.user.entity.UserRole;
import com.importer_ecommerce.importEcommerce.user.entity.UserStatus;
import com.importer_ecommerce.importEcommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Data initializer for creating initial admin users
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    
    @Override
    public void run(String... args) throws Exception {
        initializeAdminUsers();
    }
    
    /**
     * Initialize admin users
     */
    private void initializeAdminUsers() {
        // Check if admin users already exist
        List<User> existingAdmins = userRepository.findByRole(UserRole.ADMIN);
        
        if (!existingAdmins.isEmpty()) {
            log.info("Admin users already exist. Skipping initialization.");
            return;
        }
        
        // Create initial admin users
        User alamgirAdmin = User.builder()
            .name("Alamgir Hussain")
            .email("info.alamgirr@gmail.com")
            .phone("+1234567890")
            .role(UserRole.ADMIN)
            .status(UserStatus.ACTIVE)
            .build();
        
        User fahimAdmin = User.builder()
            .name("Fahim Uddin")
            .email("info.fahim@gmail.com") // Please provide the correct email address
            .phone("+1234567891")
            .role(UserRole.ADMIN)
            .status(UserStatus.ACTIVE)
            .build();
        
        try {
            userRepository.save(alamgirAdmin);
            userRepository.save(fahimAdmin);
            
            log.info("Initial admin users created successfully:");
            log.info("1. Alamgir Hussain - info.alamgirr@gmail.com");
            log.info("2. Fahim Uddin - info.fahim@gmail.com");
            
        } catch (Exception e) {
            log.error("Failed to create initial admin users", e);
        }
    }
}
