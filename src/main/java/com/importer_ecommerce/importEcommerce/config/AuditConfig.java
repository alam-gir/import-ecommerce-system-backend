package com.importer_ecommerce.importEcommerce.config;

import com.importer_ecommerce.importEcommerce.auth.security.SecurityUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

/**
 * Audit configuration for JPA entities
 * Enables automatic auditing for created_by and updated_by fields
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {
    
    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return new SpringSecurityAuditAwareImpl();
    }
    
    /**
     * Implementation of AuditorAware to get current user ID from security context
     */
    public static class SpringSecurityAuditAwareImpl implements AuditorAware<UUID> {
        
        @Override
        public Optional<UUID> getCurrentAuditor() {
            return SecurityUtil.getCurrentUserId();
        }
    }
}
