package com.importer_ecommerce.importEcommerce.auth.config;

import com.importer_ecommerce.importEcommerce.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    
    private final UserService userService;
    
    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        log.info("Starting data initialization...");
        
        try {
            // Create a test customer
            try {
                userService.createCustomer("01712345678", "Test Customer", "password123");
                log.info("Test customer created successfully");
            } catch (Exception e) {
                if (e.getMessage().contains("already exists")) {
                    log.info("Test customer already exists, skipping creation");
                } else {
                    log.error("Failed to create test customer: {}", e.getMessage());
                }
            }
            
            // Create a test admin
            try {
                userService.createAdmin("admin@example.com", "Admin User", "admin123");
                log.info("Test admin created successfully");
            } catch (Exception e) {
                if (e.getMessage().contains("already exists")) {
                    log.info("Test admin already exists, skipping creation");
                } else {
                    log.error("Failed to create test admin: {}", e.getMessage());
                }
            }
            
            // Create a test staff
            try {
                userService.createStaff("staff@example.com", "Staff User", "staff123");
                log.info("Test staff created successfully");
            } catch (Exception e) {
                if (e.getMessage().contains("already exists")) {
                    log.info("Test staff already exists, skipping creation");
                } else {
                    log.error("Failed to create test staff: {}", e.getMessage());
                }
            }
            
            log.info("Data initialization completed successfully");
            
        } catch (Exception e) {
            log.error("Critical error during data initialization: {}", e.getMessage(), e);
        }
    }
}
