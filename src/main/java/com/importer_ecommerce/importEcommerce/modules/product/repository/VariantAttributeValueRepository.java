package com.importer_ecommerce.importEcommerce.modules.product.repository;

import com.importer_ecommerce.importEcommerce.modules.product.entity.VariantAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for VariantAttributeValue entity
 */
@Repository
public interface VariantAttributeValueRepository extends JpaRepository<VariantAttributeValue, UUID> {
    
    /**
     * Find attribute values by variant attribute ID
     */
    @Query("SELECT v FROM VariantAttributeValue v WHERE v.variantAttribute.id = :attributeId AND v.isDeleted = false ORDER BY v.sortOrder, v.value")
    List<VariantAttributeValue> findByVariantAttributeId(@Param("attributeId") UUID attributeId);
    
    /**
     * Find active attribute values by variant attribute ID
     */
    @Query("SELECT v FROM VariantAttributeValue v WHERE v.variantAttribute.id = :attributeId AND v.isActive = true AND v.isDeleted = false ORDER BY v.sortOrder, v.value")
    List<VariantAttributeValue> findActiveByVariantAttributeId(@Param("attributeId") UUID attributeId);
    
    /**
     * Find attribute values by product ID
     */
    @Query("SELECT v FROM VariantAttributeValue v WHERE v.variantAttribute.product.id = :productId AND v.isDeleted = false ORDER BY v.variantAttribute.sortOrder, v.sortOrder, v.value")
    List<VariantAttributeValue> findByProductId(@Param("productId") UUID productId);
    
    /**
     * Find active attribute values by product ID
     */
    @Query("SELECT v FROM VariantAttributeValue v WHERE v.variantAttribute.product.id = :productId AND v.isActive = true AND v.isDeleted = false ORDER BY v.variantAttribute.sortOrder, v.sortOrder, v.value")
    List<VariantAttributeValue> findActiveByProductId(@Param("productId") UUID productId);
    
    /**
     * Find attribute values by value and variant attribute ID
     */
    @Query("SELECT v FROM VariantAttributeValue v WHERE v.value = :value AND v.variantAttribute.id = :attributeId AND v.isDeleted = false")
    Optional<VariantAttributeValue> findByValueAndVariantAttributeId(@Param("value") String value, @Param("attributeId") UUID attributeId);
    
    /**
     * Find attribute values by display value and variant attribute ID
     */
    @Query("SELECT v FROM VariantAttributeValue v WHERE v.displayValue = :displayValue AND v.variantAttribute.id = :attributeId AND v.isDeleted = false")
    Optional<VariantAttributeValue> findByDisplayValueAndVariantAttributeId(@Param("displayValue") String displayValue, @Param("attributeId") UUID attributeId);
    
    /**
     * Find attribute values with images
     */
    @Query("SELECT v FROM VariantAttributeValue v WHERE v.imageUrl IS NOT NULL AND v.imageUrl != '' AND v.isDeleted = false ORDER BY v.value")
    List<VariantAttributeValue> findWithImages();
    
    /**
     * Find attribute values with hex colors
     */
    @Query("SELECT v FROM VariantAttributeValue v WHERE v.hexColor IS NOT NULL AND v.hexColor != '' AND v.isDeleted = false ORDER BY v.value")
    List<VariantAttributeValue> findWithHexColors();
    
    /**
     * Find attribute values by hex color
     */
    @Query("SELECT v FROM VariantAttributeValue v WHERE v.hexColor = :hexColor AND v.isDeleted = false ORDER BY v.value")
    List<VariantAttributeValue> findByHexColor(@Param("hexColor") String hexColor);
    
    /**
     * Find attribute values by value pattern
     */
    @Query("SELECT v FROM VariantAttributeValue v WHERE v.value LIKE %:valuePattern% AND v.isDeleted = false ORDER BY v.value")
    List<VariantAttributeValue> findByValueContaining(@Param("valuePattern") String valuePattern);
    
    /**
     * Check if value exists for attribute (excluding current value)
     */
    @Query("SELECT COUNT(v) > 0 FROM VariantAttributeValue v WHERE v.value = :value AND v.variantAttribute.id = :attributeId AND v.isDeleted = false AND (:excludeId IS NULL OR v.id != :excludeId)")
    boolean existsByValueAndVariantAttributeIdAndNotDeleted(@Param("value") String value, @Param("attributeId") UUID attributeId, @Param("excludeId") UUID excludeId);
    
    /**
     * Count attribute values by variant attribute
     */
    @Query("SELECT COUNT(v) FROM VariantAttributeValue v WHERE v.variantAttribute.id = :attributeId AND v.isDeleted = false")
    long countByVariantAttributeId(@Param("attributeId") UUID attributeId);
    
    /**
     * Find attribute values by variant attribute and active status
     */
    @Query("SELECT v FROM VariantAttributeValue v WHERE v.variantAttribute.id = :attributeId AND v.isActive = :isActive AND v.isDeleted = false ORDER BY v.sortOrder, v.value")
    List<VariantAttributeValue> findByVariantAttributeIdAndIsActive(@Param("attributeId") UUID attributeId, @Param("isActive") Boolean isActive);
    
    /**
     * Find all unique values for an attribute type
     */
    @Query("SELECT DISTINCT v.value FROM VariantAttributeValue v WHERE v.variantAttribute.attributeType = :attributeType AND v.isDeleted = false ORDER BY v.value")
    List<String> findDistinctValuesByAttributeType(@Param("attributeType") com.importer_ecommerce.importEcommerce.modules.product.entity.VariantAttribute.AttributeType attributeType);
}
