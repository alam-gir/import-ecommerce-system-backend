package com.importer_ecommerce.importEcommerce.auth.repository;

import com.importer_ecommerce.importEcommerce.auth.entity.CustomerProfile;
import com.importer_ecommerce.importEcommerce.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {
    
    Optional<CustomerProfile> findByUser(User user);
    
    Optional<CustomerProfile> findByEmail(String email);
    
    boolean existsByEmail(String email);
}

