package com.importer_ecommerce.importEcommerce.modules.product.repository;

import com.importer_ecommerce.importEcommerce.modules.product.entity.VariantAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for VariantAttribute entity
 */
@Repository
public interface VariantAttributeRepository extends JpaRepository<VariantAttribute, UUID> {
    
    /**
     * Find attributes by product ID
     */
    @Query("SELECT a FROM VariantAttribute a WHERE a.product.id = :productId AND a.isDeleted = false ORDER BY a.sortOrder, a.name")
    List<VariantAttribute> findByProductId(@Param("productId") UUID productId);
    
    /**
     * Find active attributes by product ID
     */
    @Query("SELECT a FROM VariantAttribute a WHERE a.product.id = :productId AND a.isActive = true AND a.isDeleted = false ORDER BY a.sortOrder, a.name")
    List<VariantAttribute> findActiveByProductId(@Param("productId") UUID productId);
    
    /**
     * Find required attributes by product ID
     */
    @Query("SELECT a FROM VariantAttribute a WHERE a.product.id = :productId AND a.isRequired = true AND a.isDeleted = false ORDER BY a.sortOrder, a.name")
    List<VariantAttribute> findRequiredByProductId(@Param("productId") UUID productId);
    
    /**
     * Find attributes by name and product ID
     */
    @Query("SELECT a FROM VariantAttribute a WHERE a.name = :name AND a.product.id = :productId AND a.isDeleted = false")
    Optional<VariantAttribute> findByNameAndProductId(@Param("name") String name, @Param("productId") UUID productId);
    
    /**
     * Find attributes by attribute type
     */
    @Query("SELECT a FROM VariantAttribute a WHERE a.attributeType = :attributeType AND a.isDeleted = false ORDER BY a.name")
    List<VariantAttribute> findByAttributeType(@Param("attributeType") VariantAttribute.AttributeType attributeType);
    
    /**
     * Find attributes by product and attribute type
     */
    @Query("SELECT a FROM VariantAttribute a WHERE a.product.id = :productId AND a.attributeType = :attributeType AND a.isDeleted = false ORDER BY a.sortOrder, a.name")
    List<VariantAttribute> findByProductIdAndAttributeType(@Param("productId") UUID productId, @Param("attributeType") VariantAttribute.AttributeType attributeType);
    
    /**
     * Check if attribute name exists for product (excluding current attribute)
     */
    @Query("SELECT COUNT(a) > 0 FROM VariantAttribute a WHERE a.name = :name AND a.product.id = :productId AND a.isDeleted = false AND (:excludeId IS NULL OR a.id != :excludeId)")
    boolean existsByNameAndProductIdAndNotDeleted(@Param("name") String name, @Param("productId") UUID productId, @Param("excludeId") UUID excludeId);
    
    /**
     * Count attributes by product
     */
    @Query("SELECT COUNT(a) FROM VariantAttribute a WHERE a.product.id = :productId AND a.isDeleted = false")
    long countByProductId(@Param("productId") UUID productId);
    
    /**
     * Find attributes by product and active status
     */
    @Query("SELECT a FROM VariantAttribute a WHERE a.product.id = :productId AND a.isActive = :isActive AND a.isDeleted = false ORDER BY a.sortOrder, a.name")
    List<VariantAttribute> findByProductIdAndIsActive(@Param("productId") UUID productId, @Param("isActive") Boolean isActive);
    
    /**
     * Find all unique attribute names
     */
    @Query("SELECT DISTINCT a.name FROM VariantAttribute a WHERE a.isDeleted = false ORDER BY a.name")
    List<String> findDistinctNames();
    
    /**
     * Find attributes by name pattern
     */
    @Query("SELECT a FROM VariantAttribute a WHERE a.name LIKE %:namePattern% AND a.isDeleted = false ORDER BY a.name")
    List<VariantAttribute> findByNameContaining(@Param("namePattern") String namePattern);
}
