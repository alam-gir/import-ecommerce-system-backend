package com.importer_ecommerce.importEcommerce.modules.inventory.repository;

import com.importer_ecommerce.importEcommerce.modules.inventory.entity.InventoryMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Repository for InventoryMovement entity
 * Handles all database operations for stock movement tracking
 */
@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID> {
    
    /**
     * Find movements by product ID with pagination
     */
    @Query("SELECT im FROM InventoryMovement im WHERE im.product.id = :productId ORDER BY im.createdAt DESC")
    Page<InventoryMovement> findByProductId(@Param("productId") UUID productId, Pageable pageable);
    
    /**
     * Find movements by variant ID with pagination
     */
    @Query("SELECT im FROM InventoryMovement im WHERE im.variant.id = :variantId ORDER BY im.createdAt DESC")
    Page<InventoryMovement> findByVariantId(@Param("variantId") UUID variantId, Pageable pageable);
    
    /**
     * Find movements by date range with pagination
     */
    @Query("SELECT im FROM InventoryMovement im WHERE im.createdAt BETWEEN :from AND :to ORDER BY im.createdAt DESC")
    Page<InventoryMovement> findByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);
    
    /**
     * Find movements by product and date range
     */
    @Query("SELECT im FROM InventoryMovement im WHERE im.product.id = :productId AND im.createdAt BETWEEN :from AND :to ORDER BY im.createdAt DESC")
    Page<InventoryMovement> findByProductAndDateRange(@Param("productId") UUID productId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);
    
    /**
     * Find movements by variant and date range
     */
    @Query("SELECT im FROM InventoryMovement im WHERE im.variant.id = :variantId AND im.createdAt BETWEEN :from AND :to ORDER BY im.createdAt DESC")
    Page<InventoryMovement> findByVariantAndDateRange(@Param("variantId") UUID variantId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);
    
    /**
     * Find movements by movement type
     */
    @Query("SELECT im FROM InventoryMovement im WHERE im.movementType = :movementType ORDER BY im.createdAt DESC")
    Page<InventoryMovement> findByMovementType(@Param("movementType") InventoryMovement.MovementType movementType, Pageable pageable);
    
    /**
     * Get total sales quantity for a variant in date range
     */
    @Query("SELECT COALESCE(SUM(im.quantity), 0) FROM InventoryMovement im WHERE im.variant.id = :variantId AND im.movementType = 'SALE' AND im.createdAt BETWEEN :from AND :to")
    Integer getTotalSalesQuantity(@Param("variantId") UUID variantId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
    
    /**
     * Get total returns quantity for a variant in date range
     */
    @Query("SELECT COALESCE(SUM(ABS(im.quantity)), 0) FROM InventoryMovement im WHERE im.variant.id = :variantId AND im.movementType = 'RETURN' AND im.createdAt BETWEEN :from AND :to")
    Integer getTotalReturnsQuantity(@Param("variantId") UUID variantId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
    
    /**
     * Get total adjustments quantity for a variant in date range
     */
    @Query("SELECT COALESCE(SUM(im.quantity), 0) FROM InventoryMovement im WHERE im.variant.id = :variantId AND im.movementType = 'ADJUSTMENT' AND im.createdAt BETWEEN :from AND :to")
    Integer getTotalAdjustmentsQuantity(@Param("variantId") UUID variantId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
    
    /**
     * Get last movement date for a variant
     */
    @Query("SELECT MAX(im.createdAt) FROM InventoryMovement im WHERE im.variant.id = :variantId AND im.movementType = 'SALE'")
    LocalDateTime getLastSaleDate(@Param("variantId") UUID variantId);
    
    /**
     * Get average daily sales for a variant in date range
     */
    @Query("SELECT COALESCE(AVG(daily_sales.sales), 0) FROM (" +
           "SELECT DATE(im.createdAt) as sale_date, SUM(ABS(im.quantity)) as sales " +
           "FROM InventoryMovement im " +
           "WHERE im.variant.id = :variantId AND im.movementType = 'SALE' AND im.createdAt BETWEEN :from AND :to " +
           "GROUP BY DATE(im.createdAt)" +
           ") daily_sales")
    Double getAverageDailySales(@Param("variantId") UUID variantId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
