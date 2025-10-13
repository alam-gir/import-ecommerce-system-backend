package com.importer_ecommerce.importEcommerce.modules.product.repository;

import com.importer_ecommerce.importEcommerce.modules.product.entity.VariantAttribute;
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
 * Repository for VariantAttribute entity
 * Handles all database operations for product variant attributes (Color, Size, etc.)
 */
@Repository
public interface VariantAttributeRepository extends JpaRepository<VariantAttribute, UUID> {

    /**
     * Find variant attribute by product and name
     * Used to check if attribute name already exists for a product
     */
    Optional<VariantAttribute> findByProductIdAndNameIgnoreCase(UUID productId, String name);

    /**
     * Find all variant attributes for a given product
     * Returns attributes ordered by name
     */
    @Query("SELECT va FROM VariantAttribute va WHERE va.product.id = :productId ORDER BY va.name")
    List<VariantAttribute> findByProductIdAndNotDeleted(@Param("productId") UUID productId);

    /**
     * Find all variant attributes for a given product with pagination
     */
    @Query("SELECT va FROM VariantAttribute va WHERE va.product.id = :productId ORDER BY va.name")
    Page<VariantAttribute> findByProductIdAndNotDeleted(@Param("productId") UUID productId, Pageable pageable);

    /**
     * Check if a variant attribute name already exists for a product
     * Used for update operations to prevent duplicate attribute names
     */
    @Query("SELECT COUNT(va) > 0 FROM VariantAttribute va WHERE va.product.id = :productId AND va.name = :name AND (:excludeId IS NULL OR va.id != :excludeId)")
    boolean existsByProductIdAndNameAndNotDeleted(@Param("productId") UUID productId, @Param("name") String name, @Param("excludeId") UUID excludeId);

    /**
     * Find variant attribute by ID
     */
    @Query("SELECT va FROM VariantAttribute va WHERE va.id = :id")
    Optional<VariantAttribute> findByIdAndNotDeleted(@Param("id") UUID id);

    /**
     * Find attributes by attribute type
     * Returns attributes of a specific type (TEXT, IMAGE, NUMBER)
     */
    @Query("SELECT va FROM VariantAttribute va WHERE va.attributeType = :attributeType ORDER BY va.name")
    List<VariantAttribute> findByAttributeType(@Param("attributeType") VariantAttribute.AttributeType attributeType);

    /**
     * Find attributes by product and attribute type
     * Returns attributes of a specific type for a specific product
     */
    @Query("SELECT va FROM VariantAttribute va WHERE va.product.id = :productId AND va.attributeType = :attributeType ORDER BY va.name")
    List<VariantAttribute> findByProductIdAndAttributeType(@Param("productId") UUID productId, @Param("attributeType") VariantAttribute.AttributeType attributeType);

    /**
     * Count attributes for a product
     * Returns the number of attributes for a specific product
     */
    @Query("SELECT COUNT(va) FROM VariantAttribute va WHERE va.product.id = :productId")
    long countByProductId(@Param("productId") UUID productId);

    /**
     * Find attributes with values
     * Returns attributes that have at least one value
     */
    @Query("SELECT va FROM VariantAttribute va WHERE SIZE(va.attributeValues) > 0 ORDER BY va.name")
    List<VariantAttribute> findAttributesWithValues();

    /**
     * Find attributes without values
     * Returns attributes that have no values
     */
    @Query("SELECT va FROM VariantAttribute va WHERE SIZE(va.attributeValues) = 0 ORDER BY va.name")
    List<VariantAttribute> findAttributesWithoutValues();

    /**
     * Find attributes by product with values count
     * Returns attributes for a product with the count of their values
     */
    @Query("SELECT va FROM VariantAttribute va WHERE va.product.id = :productId ORDER BY va.name")
    List<VariantAttribute> findByProductIdWithValueCount(@Param("productId") UUID productId);

    /**
     * Find attributes used by variants
     * Returns attributes that are actually used by product variants
     */
    @Query("SELECT DISTINCT va FROM VariantAttribute va JOIN va.attributeValues av JOIN av.variants v WHERE v.isActive = true ORDER BY va.name")
    List<VariantAttribute> findAttributesUsedByVariants();
}