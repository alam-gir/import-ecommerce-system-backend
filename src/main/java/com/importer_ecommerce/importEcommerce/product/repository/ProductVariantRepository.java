package com.importer_ecommerce.importEcommerce.product.repository;

import com.importer_ecommerce.importEcommerce.product.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    
    Optional<ProductVariant> findBySku(String sku);
    
    boolean existsBySku(String sku);
    
    boolean existsBySkuAndIdNot(String sku, UUID id);
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId")
    Page<ProductVariant> findByProductId(@Param("productId") UUID productId, Pageable pageable);
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.isActive = true")
    List<ProductVariant> findActiveByProductId(@Param("productId") UUID productId);
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.inventoryQuantity <= pv.lowStockThreshold AND pv.inventoryTracking = 'TRACKED'")
    Page<ProductVariant> findLowStockVariants(Pageable pageable);
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.inventoryQuantity = 0 AND pv.inventoryTracking = 'TRACKED'")
    Page<ProductVariant> findOutOfStockVariants(Pageable pageable);
    
    @Query("SELECT pv FROM ProductVariant pv WHERE " +
           "LOWER(pv.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(pv.sku) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<ProductVariant> searchVariants(@Param("query") String query, Pageable pageable);
}
