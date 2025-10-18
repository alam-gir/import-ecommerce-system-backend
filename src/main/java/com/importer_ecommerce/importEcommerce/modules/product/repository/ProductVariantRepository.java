package com.importer_ecommerce.importEcommerce.modules.product.repository;

import com.importer_ecommerce.importEcommerce.modules.product.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ProductVariant entity
 * Handles all database operations for product variants (SKUs)
 */
@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    /**
     * Find product variant by SKU
     * Used to check if SKU already exists
     */
    Optional<ProductVariant> findBySku(String sku);

    /**
     * Find all active product variants for a given product
     * Returns variants that are active
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.isActive = true ORDER BY pv.sku")
    List<ProductVariant> findActiveByProductId(@Param("productId") UUID productId);

    /**
     * Find all product variants for a given product (including inactive)
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId ORDER BY pv.sku")
    List<ProductVariant> findByProductIdAndNotDeleted(@Param("productId") UUID productId);

    /**
     * Find all product variants for a given product with pagination
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId ORDER BY pv.sku")
    Page<ProductVariant> findByProductIdAndNotDeleted(@Param("productId") UUID productId, Pageable pageable);

    /**
     * Check if a SKU already exists (excluding a specific variant ID)
     * Used for update operations to prevent duplicate SKUs
     */
    @Query("SELECT COUNT(pv) > 0 FROM ProductVariant pv WHERE pv.sku = :sku AND (:excludeId IS NULL OR pv.id != :excludeId)")
    boolean existsBySkuAndNotDeleted(@Param("sku") String sku, @Param("excludeId") UUID excludeId);

    /**
     * Find product variant by ID
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.id = :id")
    Optional<ProductVariant> findByIdAndNotDeleted(@Param("id") UUID id);

    /**
     * Find variants with low stock
     * Returns variants where stock is below the threshold
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.stockQuantity <= pv.lowStockThreshold AND pv.isActive = true ORDER BY pv.stockQuantity")
    List<ProductVariant> findVariantsWithLowStock();

    /**
     * Find variants out of stock
     * Returns variants with zero or negative stock
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.stockQuantity <= 0 AND pv.isActive = true ORDER BY pv.sku")
    List<ProductVariant> findVariantsOutOfStock();

    /**
     * Find variants in stock
     * Returns variants with positive stock
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.stockQuantity > 0 AND pv.isActive = true ORDER BY pv.sku")
    List<ProductVariant> findVariantsInStock();

    /**
     * Find variants by price range
     * Returns variants within the specified price range
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.price BETWEEN :minPrice AND :maxPrice AND pv.isActive = true ORDER BY pv.price")
    List<ProductVariant> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Find variants with discount (compare at price > price)
     * Returns variants that have a compare at price higher than current price
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.compareAtPrice IS NOT NULL AND pv.compareAtPrice > pv.price AND pv.isActive = true ORDER BY pv.sku")
    List<ProductVariant> findVariantsWithDiscount();

    /**
     * Find variants by attribute value ID
     * Returns variants that have a specific attribute value
     */
    @Query("SELECT pv FROM ProductVariant pv JOIN pv.attributeValues av WHERE av.id = :attributeValueId ORDER BY pv.sku")
    List<ProductVariant> findByAttributeValueId(@Param("attributeValueId") UUID attributeValueId);

    /**
     * Find variants by multiple attribute value IDs
     * Returns variants that have all the specified attribute values
     */
    @Query("SELECT pv FROM ProductVariant pv JOIN pv.attributeValues av WHERE av.id IN :attributeValueIds GROUP BY pv HAVING COUNT(DISTINCT av.id) = :expectedCount ORDER BY pv.sku")
    List<ProductVariant> findByAttributeValueIds(@Param("attributeValueIds") List<UUID> attributeValueIds, @Param("expectedCount") Long expectedCount);
    
    // Inventory Management Methods
    
    /**
     * Find variants with low stock below threshold
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.stockQuantity <= :threshold AND pv.isActive = true ORDER BY pv.stockQuantity")
    Page<ProductVariant> findLowStockVariants(@Param("threshold") Integer threshold, Pageable pageable);
    
    /**
     * Find variants that are out of stock
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.stockQuantity <= 0 AND pv.isActive = true ORDER BY pv.sku")
    Page<ProductVariant> findOutOfStockVariants(Pageable pageable);
    
    /**
     * Count variants with low stock
     */
    @Query("SELECT COUNT(pv) FROM ProductVariant pv WHERE pv.stockQuantity <= :threshold AND pv.isActive = true")
    int countLowStockVariants(@Param("threshold") Integer threshold);
    
    /**
     * Count variants that are out of stock
     */
    @Query("SELECT COUNT(pv) FROM ProductVariant pv WHERE pv.stockQuantity <= 0 AND pv.isActive = true")
    int countOutOfStockVariants();
    
    /**
     * Get total stock quantity across all variants
     */
    @Query("SELECT COALESCE(SUM(pv.stockQuantity), 0) FROM ProductVariant pv WHERE pv.isActive = true")
    int getTotalStockQuantity();
    
    /**
     * Get total stock value across all variants (stock quantity * price)
     */
    @Query("SELECT COALESCE(SUM(pv.stockQuantity * pv.price), 0) FROM ProductVariant pv WHERE pv.isActive = true")
    BigDecimal getTotalStockValue();
}