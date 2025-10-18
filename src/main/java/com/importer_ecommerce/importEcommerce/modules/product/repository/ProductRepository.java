package com.importer_ecommerce.importEcommerce.modules.product.repository;

import com.importer_ecommerce.importEcommerce.modules.product.entity.Product;
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
 * Repository for Product entity
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    
    /**
     * Find product by title (case insensitive)
     */
    Optional<Product> findByTitleIgnoreCase(String title);
    
    /**
     * Find products by title containing (case insensitive) with pagination
     */
    Page<Product> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    /**
     * Find products by title containing and category with pagination
     */
    @Query("SELECT p FROM Product p WHERE p.title LIKE %:title% AND p.category.id = :categoryId ORDER BY p.title")
    Page<Product> findByTitleContainingIgnoreCaseAndCategoryId(@Param("title") String title, @Param("categoryId") UUID categoryId, Pageable pageable);
    
    /**
     * Find all products
     */
    @Query("SELECT p FROM Product p ORDER BY p.title")
    List<Product> findAllActive();
    
    /**
     * Find all products with pagination
     */
    @Query("SELECT p FROM Product p ORDER BY p.title")
    Page<Product> findAllActive(Pageable pageable);
    
    /**
     * Find products by category
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId ORDER BY p.title")
    List<Product> findByCategoryId(@Param("categoryId") UUID categoryId);
    
    /**
     * Find products by category with pagination
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId ORDER BY p.title")
    Page<Product> findByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);
    
    /**
     * Find products with minimum order quantity
     */
    @Query("SELECT p FROM Product p WHERE p.minimumOrderQuantity IS NOT NULL AND p.minimumOrderQuantity > 0 ORDER BY p.title")
    List<Product> findProductsWithMinimumOrderQuantity();
    
    /**
     * Find products without minimum order quantity
     */
    @Query("SELECT p FROM Product p WHERE (p.minimumOrderQuantity IS NULL OR p.minimumOrderQuantity = 0) ORDER BY p.title")
    List<Product> findProductsWithoutMinimumOrderQuantity();
    
    /**
     * Find products by minimum order quantity range
     */
    @Query("SELECT p FROM Product p WHERE p.minimumOrderQuantity BETWEEN :minQuantity AND :maxQuantity ORDER BY p.title")
    List<Product> findByMinimumOrderQuantityBetween(@Param("minQuantity") Integer minQuantity, @Param("maxQuantity") Integer maxQuantity);
    
    /**
     * Check if product title exists (excluding current product)
     */
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.title = :title AND (:excludeId IS NULL OR p.id != :excludeId)")
    boolean existsByTitleAndNotDeleted(@Param("title") String title, @Param("excludeId") UUID excludeId);
    
    /**
     * Find product by ID
     */
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdAndNotDeleted(@Param("id") UUID id);
    
    /**
     * Count products by category
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    long countByCategoryId(@Param("categoryId") UUID categoryId);
    
    /**
     * Find products with images
     */
    @Query("SELECT p FROM Product p WHERE SIZE(p.images) > 0 ORDER BY p.title")
    List<Product> findProductsWithImages();
    
    /**
     * Find products with profile image
     */
    @Query("SELECT p FROM Product p WHERE p.profileImage IS NOT NULL AND p.profileImage != '' ORDER BY p.title")
    List<Product> findProductsWithProfileImage();

    /**
     * Find products with low stock variants
     */
    @Query("SELECT DISTINCT p FROM Product p JOIN p.variants v WHERE v.stockQuantity <= v.lowStockThreshold ORDER BY p.title")
    List<Product> findProductsWithLowStockVariants();

    /**
     * Find products without variants
     */
    @Query("SELECT p FROM Product p WHERE SIZE(p.variants) = 0 ORDER BY p.title")
    List<Product> findProductsWithoutVariants();

    /**
     * Find products with variants
     */
    @Query("SELECT p FROM Product p WHERE SIZE(p.variants) > 0 ORDER BY p.title")
    List<Product> findProductsWithVariants();
    
    /**
     * Check if product slug exists
     */
    boolean existsBySlug(String slug);
    
    /**
     * Check if product slug exists (excluding current product)
     */
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.slug = :slug AND (:excludeId IS NULL OR p.id != :excludeId)")
    boolean existsBySlugAndIdNot(@Param("slug") String slug, @Param("excludeId") UUID excludeId);
    
    /**
     * Find product by slug
     */
    Optional<Product> findBySlug(String slug);
}
