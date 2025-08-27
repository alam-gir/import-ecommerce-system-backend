package com.importer_ecommerce.importEcommerce.auth.config;

import com.importer_ecommerce.importEcommerce.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserService userService;
    
    @Override
    public void run(String... args) throws Exception {
        try {
            // Create a test customer
            userService.createCustomer("01712345678", "Test Customer", "password123");
            log.info("Test customer created successfully");
            
            // Create a test admin
            userService.createStaff("admin@example.com", "Admin User", "admin123");
            log.info("Test admin created successfully");
            
            // Create a test staff
            userService.createStaff("staff@example.com", "Staff User", "staff123");
            log.info("Test staff created successfully");
            
        } catch (Exception e) {
            log.warn("Could not create test users: {}", e.getMessage());
        }
    }
}
