package com.importer_ecommerce.importEcommerce.modules.inventory.repository;

import com.importer_ecommerce.importEcommerce.modules.inventory.entity.InventoryAlertConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for InventoryAlertConfig entity
 * Handles alert configuration operations
 */
@Repository
public interface InventoryAlertConfigRepository extends JpaRepository<InventoryAlertConfig, UUID> {
    
    /**
     * Find the active alert configuration
     * Assumes there's only one active configuration
     */
    @Query("SELECT iac FROM InventoryAlertConfig iac ORDER BY iac.createdAt DESC")
    Optional<InventoryAlertConfig> findActiveConfig();
}
