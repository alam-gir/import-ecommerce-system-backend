package com.importer_ecommerce.importEcommerce.modules.product.repository;

import com.importer_ecommerce.importEcommerce.modules.product.entity.ProductSpecification;
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
 * Repository for ProductSpecification entity
 * Handles all database operations for product specifications (Material, Care instructions, etc.)
 */
@Repository
public interface ProductSpecificationRepository extends JpaRepository<ProductSpecification, UUID> {

    /**
     * Find product specification by product and name
     * Used to check if specification name already exists for a product
     */
    Optional<ProductSpecification> findByProductIdAndNameIgnoreCase(UUID productId, String name);

    /**
     * Find all product specifications for a given product
     * Returns specifications ordered by name
     */
    @Query("SELECT ps FROM ProductSpecification ps WHERE ps.product.id = :productId ORDER BY ps.name")
    List<ProductSpecification> findByProductIdAndNotDeleted(@Param("productId") UUID productId);

    /**
     * Find all product specifications for a given product with pagination
     */
    @Query("SELECT ps FROM ProductSpecification ps WHERE ps.product.id = :productId ORDER BY ps.name")
    Page<ProductSpecification> findByProductIdAndNotDeleted(@Param("productId") UUID productId, Pageable pageable);

    /**
     * Check if a product specification name already exists for a product
     * Used for update operations to prevent duplicate specification names
     */
    @Query("SELECT COUNT(ps) > 0 FROM ProductSpecification ps WHERE ps.product.id = :productId AND ps.name = :name AND (:excludeId IS NULL OR ps.id != :excludeId)")
    boolean existsByProductIdAndNameAndNotDeleted(@Param("productId") UUID productId, @Param("name") String name, @Param("excludeId") UUID excludeId);

    /**
     * Find product specification by ID
     */
    @Query("SELECT ps FROM ProductSpecification ps WHERE ps.id = :id")
    Optional<ProductSpecification> findByIdAndNotDeleted(@Param("id") UUID id);

    /**
     * Find specifications by name across all products
     * Returns specifications with a specific name from all products
     */
    @Query("SELECT ps FROM ProductSpecification ps WHERE ps.name = :name ORDER BY ps.product.title, ps.name")
    List<ProductSpecification> findByNameAndNotDeleted(@Param("name") String name);

    /**
     * Find specifications by name pattern
     * Returns specifications with names containing the search term
     */
    @Query("SELECT ps FROM ProductSpecification ps WHERE ps.name LIKE %:searchTerm% ORDER BY ps.name")
    List<ProductSpecification> findByNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    /**
     * Count specifications for a product
     * Returns the number of specifications for a specific product
     */
    @Query("SELECT COUNT(ps) FROM ProductSpecification ps WHERE ps.product.id = :productId")
    long countByProductId(@Param("productId") UUID productId);

    /**
     * Find specifications with long values
     * Returns specifications with values longer than the specified length
     */
    @Query("SELECT ps FROM ProductSpecification ps WHERE LENGTH(ps.value) > :minLength ORDER BY LENGTH(ps.value) DESC")
    List<ProductSpecification> findSpecificationsWithLongValues(@Param("minLength") int minLength);

    /**
     * Find specifications by value pattern
     * Returns specifications with values containing the search term
     */
    @Query("SELECT ps FROM ProductSpecification ps WHERE ps.value LIKE %:searchTerm% ORDER BY ps.name")
    List<ProductSpecification> findByValueContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    /**
     * Find all unique specification names across all products
     * Used for filtering and search functionality
     */
    @Query("SELECT DISTINCT ps.name FROM ProductSpecification ps ORDER BY ps.name")
    List<String> findDistinctNames();

    /**
     * Find specifications by multiple product IDs
     * Returns specifications for multiple products at once
     */
    @Query("SELECT ps FROM ProductSpecification ps WHERE ps.product.id IN :productIds ORDER BY ps.product.title, ps.name")
    List<ProductSpecification> findByProductIds(@Param("productIds") List<UUID> productIds);

    /**
     * Find specifications by category
     * Returns specifications for products in a specific category
     */
    @Query("SELECT ps FROM ProductSpecification ps WHERE ps.product.category.id = :categoryId ORDER BY ps.product.title, ps.name")
    List<ProductSpecification> findByCategoryId(@Param("categoryId") UUID categoryId);

    /**
     * Find specifications by category with pagination
     */
    @Query("SELECT ps FROM ProductSpecification ps WHERE ps.product.category.id = :categoryId ORDER BY ps.product.title, ps.name")
    Page<ProductSpecification> findByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);
}