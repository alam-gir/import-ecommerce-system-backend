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
 */
@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    
    /**
     * Find variants by product ID
     */
    @Query("SELECT v FROM ProductVariant v WHERE v.product.id = :productId AND v.isDeleted = false ORDER BY v.sku")
    List<ProductVariant> findByProductId(@Param("productId") UUID productId);
    
    /**
     * Find variants by product ID with pagination
     */
    @Query("SELECT v FROM ProductVariant v WHERE v.product.id = :productId AND v.isDeleted = false ORDER BY v.sku")
    Page<ProductVariant> findByProductId(@Param("productId") UUID productId, Pageable pageable);
    
    /**
     * Find variant by SKU
     */
    Optional<ProductVariant> findBySku(String sku);
    
    /**
     * Find variant by SKU and not deleted
     */
    @Query("SELECT v FROM ProductVariant v WHERE v.sku = :sku AND v.isDeleted = false")
    Optional<ProductVariant> findBySkuAndNotDeleted(@Param("sku") String sku);
    
    /**
     * Find variants by price range
     */
    @Query("SELECT v FROM ProductVariant v WHERE v.price BETWEEN :minPrice AND :maxPrice AND v.isDeleted = false ORDER BY v.price")
    List<ProductVariant> findByPriceBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * Find variants in stock
     */
    @Query("SELECT v FROM ProductVariant v WHERE v.stockQuantity > 0 AND v.isDeleted = false ORDER BY v.sku")
    List<ProductVariant> findInStock();
    
    /**
     * Find variants out of stock
     */
    @Query("SELECT v FROM ProductVariant v WHERE v.stockQuantity <= 0 AND v.isDeleted = false ORDER BY v.sku")
    List<ProductVariant> findOutOfStock();
    
    /**
     * Find variants with low stock
     */
    @Query("SELECT v FROM ProductVariant v WHERE v.stockQuantity <= v.lowStockThreshold AND v.isDeleted = false ORDER BY v.stockQuantity")
    List<ProductVariant> findLowStock();
    
    /**
     * Find active variants
     */
    @Query("SELECT v FROM ProductVariant v WHERE v.isActive = true AND v.isDeleted = false ORDER BY v.sku")
    List<ProductVariant> findActive();
    
    /**
     * Find inactive variants
     */
    @Query("SELECT v FROM ProductVariant v WHERE v.isActive = false AND v.isDeleted = false ORDER BY v.sku")
    List<ProductVariant> findInactive();
    
    /**
     * Find variants with discount (compare at price > price)
     */
    @Query("SELECT v FROM ProductVariant v WHERE v.compareAtPrice > v.price AND v.isDeleted = false ORDER BY v.price")
    List<ProductVariant> findWithDiscount();
    
    /**
     * Find variants by barcode
     */
    Optional<ProductVariant> findByBarcode(String barcode);
    
    /**
     * Check if SKU exists (excluding current variant)
     */
    @Query("SELECT COUNT(v) > 0 FROM ProductVariant v WHERE v.sku = :sku AND v.isDeleted = false AND (:excludeId IS NULL OR v.id != :excludeId)")
    boolean existsBySkuAndNotDeleted(@Param("sku") String sku, @Param("excludeId") UUID excludeId);
    
    /**
     * Check if barcode exists (excluding current variant)
     */
    @Query("SELECT COUNT(v) > 0 FROM ProductVariant v WHERE v.barcode = :barcode AND v.isDeleted = false AND (:excludeId IS NULL OR v.id != :excludeId)")
    boolean existsByBarcodeAndNotDeleted(@Param("barcode") String barcode, @Param("excludeId") UUID excludeId);
    
    /**
     * Count variants by product
     */
    @Query("SELECT COUNT(v) FROM ProductVariant v WHERE v.product.id = :productId AND v.isDeleted = false")
    long countByProductId(@Param("productId") UUID productId);
    
    /**
     * Find variants by product and active status
     */
    @Query("SELECT v FROM ProductVariant v WHERE v.product.id = :productId AND v.isActive = :isActive AND v.isDeleted = false ORDER BY v.sku")
    List<ProductVariant> findByProductIdAndIsActive(@Param("productId") UUID productId, @Param("isActive") Boolean isActive);
    
    /**
     * Find variants by product and stock status
     */
    @Query("SELECT v FROM ProductVariant v WHERE v.product.id = :productId AND v.stockQuantity > 0 AND v.isDeleted = false ORDER BY v.sku")
    List<ProductVariant> findByProductIdAndInStock(@Param("productId") UUID productId);
}
