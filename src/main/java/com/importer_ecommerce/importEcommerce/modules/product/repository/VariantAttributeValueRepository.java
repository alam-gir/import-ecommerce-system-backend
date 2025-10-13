package com.importer_ecommerce.importEcommerce.modules.product.repository;

import com.importer_ecommerce.importEcommerce.modules.product.entity.VariantAttributeValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for VariantAttributeValue entity
 * Handles all database operations for attribute values (Red, Blue, Small, Large, etc.)
 */
@Repository
public interface VariantAttributeValueRepository extends JpaRepository<VariantAttributeValue, UUID> {

    /**
     * Find variant attribute value by variant attribute and value
     * Used to check if value already exists for an attribute
     */
    Optional<VariantAttributeValue> findByVariantAttributeIdAndValueIgnoreCase(UUID variantAttributeId, String value);

    /**
     * Find all variant attribute values for a given variant attribute
     * Returns values ordered by value
     */
    @Query("SELECT vav FROM VariantAttributeValue vav WHERE vav.variantAttribute.id = :attributeId ORDER BY vav.value")
    List<VariantAttributeValue> findByVariantAttributeIdAndNotDeleted(@Param("attributeId") UUID attributeId);

    /**
     * Find all variant attribute values for a given variant attribute with pagination
     */
    @Query("SELECT vav FROM VariantAttributeValue vav WHERE vav.variantAttribute.id = :attributeId ORDER BY vav.value")
    Page<VariantAttributeValue> findByVariantAttributeIdAndNotDeleted(@Param("attributeId") UUID attributeId, Pageable pageable);

    /**
     * Check if a variant attribute value already exists for an attribute
     * Used for update operations to prevent duplicate values
     */
    @Query("SELECT COUNT(vav) > 0 FROM VariantAttributeValue vav WHERE vav.variantAttribute.id = :attributeId AND vav.value = :value AND (:excludeId IS NULL OR vav.id != :excludeId)")
    boolean existsByVariantAttributeIdAndValueAndNotDeleted(@Param("attributeId") UUID attributeId, @Param("value") String value, @Param("excludeId") UUID excludeId);

    /**
     * Find variant attribute value by ID
     */
    @Query("SELECT vav FROM VariantAttributeValue vav WHERE vav.id = :id")
    Optional<VariantAttributeValue> findByIdAndNotDeleted(@Param("id") UUID id);

    /**
     * Find values by attribute type
     * Returns values for attributes of a specific type (TEXT, IMAGE, NUMBER)
     */
    @Query("SELECT vav FROM VariantAttributeValue vav WHERE vav.variantAttribute.attributeType = :attributeType ORDER BY vav.value")
    List<VariantAttributeValue> findByAttributeType(@Param("attributeType") com.importer_ecommerce.importEcommerce.modules.product.entity.VariantAttribute.AttributeType attributeType);

    /**
     * Find values with images
     * Returns values that have image URLs
     */
    @Query("SELECT vav FROM VariantAttributeValue vav WHERE vav.imageUrl IS NOT NULL AND vav.imageUrl != '' ORDER BY vav.value")
    List<VariantAttributeValue> findValuesWithImages();

    /**
     * Find values without images
     * Returns values that don't have image URLs
     */
    @Query("SELECT vav FROM VariantAttributeValue vav WHERE (vav.imageUrl IS NULL OR vav.imageUrl = '') ORDER BY vav.value")
    List<VariantAttributeValue> findValuesWithoutImages();

    /**
     * Count values for an attribute
     * Returns the number of values for a specific attribute
     */
    @Query("SELECT COUNT(vav) FROM VariantAttributeValue vav WHERE vav.variantAttribute.id = :attributeId")
    long countByVariantAttributeId(@Param("attributeId") UUID attributeId);

    /**
     * Find values used by variants
     * Returns values that are actually used by product variants
     */
    @Query("SELECT DISTINCT vav FROM VariantAttributeValue vav JOIN vav.variants v WHERE v.isActive = true ORDER BY vav.value")
    List<VariantAttributeValue> findValuesUsedByVariants();

    /**
     * Find values by product
     * Returns all values for all attributes of a specific product
     */
    @Query("SELECT vav FROM VariantAttributeValue vav WHERE vav.variantAttribute.product.id = :productId ORDER BY vav.variantAttribute.name, vav.value")
    List<VariantAttributeValue> findByProductId(@Param("productId") UUID productId);

    /**
     * Find values by product and attribute type
     * Returns values for attributes of a specific type for a specific product
     */
    @Query("SELECT vav FROM VariantAttributeValue vav WHERE vav.variantAttribute.product.id = :productId AND vav.variantAttribute.attributeType = :attributeType ORDER BY vav.value")
    List<VariantAttributeValue> findByProductIdAndAttributeType(@Param("productId") UUID productId, @Param("attributeType") com.importer_ecommerce.importEcommerce.modules.product.entity.VariantAttribute.AttributeType attributeType);

    /**
     * Find all unique values for an attribute type across all products
     * Used for filtering and search functionality
     */
    @Query("SELECT DISTINCT vav.value FROM VariantAttributeValue vav WHERE vav.variantAttribute.attributeType = :attributeType ORDER BY vav.value")
    List<String> findDistinctValuesByAttributeType(@Param("attributeType") com.importer_ecommerce.importEcommerce.modules.product.entity.VariantAttribute.AttributeType attributeType);

    /**
     * Find values by multiple attribute IDs
     * Returns values for multiple attributes at once
     */
    @Query("SELECT vav FROM VariantAttributeValue vav WHERE vav.variantAttribute.id IN :attributeIds ORDER BY vav.variantAttribute.name, vav.value")
    List<VariantAttributeValue> findByVariantAttributeIds(@Param("attributeIds") List<UUID> attributeIds);
}