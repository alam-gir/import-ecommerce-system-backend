package com.importer_ecommerce.importEcommerce.modules.product.repository;

import com.importer_ecommerce.importEcommerce.modules.product.entity.ProductSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ProductSpecification entity
 */
@Repository
public interface ProductSpecificationRepository extends JpaRepository<ProductSpecification, UUID> {
    
    /**
     * Find specifications by product ID
     */
    @Query("SELECT s FROM ProductSpecification s WHERE s.product.id = :productId AND s.isDeleted = false ORDER BY s.sortOrder, s.name")
    List<ProductSpecification> findByProductId(@Param("productId") UUID productId);
    
    /**
     * Find active specifications by product ID
     */
    @Query("SELECT s FROM ProductSpecification s WHERE s.product.id = :productId AND s.isActive = true AND s.isDeleted = false ORDER BY s.sortOrder, s.name")
    List<ProductSpecification> findActiveByProductId(@Param("productId") UUID productId);
    
    /**
     * Find highlighted specifications by product ID
     */
    @Query("SELECT s FROM ProductSpecification s WHERE s.product.id = :productId AND s.isHighlighted = true AND s.isDeleted = false ORDER BY s.sortOrder, s.name")
    List<ProductSpecification> findHighlightedByProductId(@Param("productId") UUID productId);
    
    /**
     * Find specification by name and product ID
     */
    @Query("SELECT s FROM ProductSpecification s WHERE s.name = :name AND s.product.id = :productId AND s.isDeleted = false")
    Optional<ProductSpecification> findByNameAndProductId(@Param("name") String name, @Param("productId") UUID productId);
    
    /**
     * Find specifications by name pattern
     */
    @Query("SELECT s FROM ProductSpecification s WHERE s.name LIKE %:namePattern% AND s.isDeleted = false ORDER BY s.name")
    List<ProductSpecification> findByNameContaining(@Param("namePattern") String namePattern);
    
    /**
     * Find specifications by value pattern
     */
    @Query("SELECT s FROM ProductSpecification s WHERE s.value LIKE %:valuePattern% AND s.isDeleted = false ORDER BY s.name")
    List<ProductSpecification> findByValueContaining(@Param("valuePattern") String valuePattern);
    
    /**
     * Find specifications by unit
     */
    @Query("SELECT s FROM ProductSpecification s WHERE s.unit = :unit AND s.isDeleted = false ORDER BY s.name")
    List<ProductSpecification> findByUnit(@Param("unit") String unit);
    
    /**
     * Find specifications with units
     */
    @Query("SELECT s FROM ProductSpecification s WHERE s.unit IS NOT NULL AND s.unit != '' AND s.isDeleted = false ORDER BY s.name")
    List<ProductSpecification> findWithUnits();
    
    /**
     * Find specifications without units
     */
    @Query("SELECT s FROM ProductSpecification s WHERE (s.unit IS NULL OR s.unit = '') AND s.isDeleted = false ORDER BY s.name")
    List<ProductSpecification> findWithoutUnits();
    
    /**
     * Check if specification name exists for product (excluding current specification)
     */
    @Query("SELECT COUNT(s) > 0 FROM ProductSpecification s WHERE s.name = :name AND s.product.id = :productId AND s.isDeleted = false AND (:excludeId IS NULL OR s.id != :excludeId)")
    boolean existsByNameAndProductIdAndNotDeleted(@Param("name") String name, @Param("productId") UUID productId, @Param("excludeId") UUID excludeId);
    
    /**
     * Count specifications by product
     */
    @Query("SELECT COUNT(s) FROM ProductSpecification s WHERE s.product.id = :productId AND s.isDeleted = false")
    long countByProductId(@Param("productId") UUID productId);
    
    /**
     * Find specifications by product and active status
     */
    @Query("SELECT s FROM ProductSpecification s WHERE s.product.id = :productId AND s.isActive = :isActive AND s.isDeleted = false ORDER BY s.sortOrder, s.name")
    List<ProductSpecification> findByProductIdAndIsActive(@Param("productId") UUID productId, @Param("isActive") Boolean isActive);
    
    /**
     * Find specifications by product and highlighted status
     */
    @Query("SELECT s FROM ProductSpecification s WHERE s.product.id = :productId AND s.isHighlighted = :isHighlighted AND s.isDeleted = false ORDER BY s.sortOrder, s.name")
    List<ProductSpecification> findByProductIdAndIsHighlighted(@Param("productId") UUID productId, @Param("isHighlighted") Boolean isHighlighted);
    
    /**
     * Find all unique specification names
     */
    @Query("SELECT DISTINCT s.name FROM ProductSpecification s WHERE s.isDeleted = false ORDER BY s.name")
    List<String> findDistinctNames();
    
    /**
     * Find all unique units
     */
    @Query("SELECT DISTINCT s.unit FROM ProductSpecification s WHERE s.unit IS NOT NULL AND s.unit != '' AND s.isDeleted = false ORDER BY s.unit")
    List<String> findDistinctUnits();
}
