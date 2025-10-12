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
     * Find products by title containing (case insensitive)
     */
    List<Product> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Find all active products (not deleted)
     */
    @Query("SELECT p FROM Product p WHERE p.isDeleted = false ORDER BY p.title")
    List<Product> findAllActive();
    
    /**
     * Find all active products with pagination
     */
    @Query("SELECT p FROM Product p WHERE p.isDeleted = false ORDER BY p.title")
    Page<Product> findAllActive(Pageable pageable);
    
    /**
     * Find products by category
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.isDeleted = false ORDER BY p.title")
    List<Product> findByCategoryId(@Param("categoryId") UUID categoryId);
    
    /**
     * Find products by category with pagination
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.isDeleted = false ORDER BY p.title")
    Page<Product> findByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);
    
    /**
     * Find products with minimum order quantity
     */
    @Query("SELECT p FROM Product p WHERE p.minimumOrderQuantity IS NOT NULL AND p.minimumOrderQuantity > 0 AND p.isDeleted = false ORDER BY p.title")
    List<Product> findProductsWithMinimumOrderQuantity();
    
    /**
     * Find products without minimum order quantity
     */
    @Query("SELECT p FROM Product p WHERE (p.minimumOrderQuantity IS NULL OR p.minimumOrderQuantity = 0) AND p.isDeleted = false ORDER BY p.title")
    List<Product> findProductsWithoutMinimumOrderQuantity();
    
    /**
     * Find products by minimum order quantity range
     */
    @Query("SELECT p FROM Product p WHERE p.minimumOrderQuantity BETWEEN :minQuantity AND :maxQuantity AND p.isDeleted = false ORDER BY p.title")
    List<Product> findByMinimumOrderQuantityBetween(@Param("minQuantity") Integer minQuantity, @Param("maxQuantity") Integer maxQuantity);
    
    /**
     * Check if product title exists (excluding current product)
     */
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.title = :title AND p.isDeleted = false AND (:excludeId IS NULL OR p.id != :excludeId)")
    boolean existsByTitleAndNotDeleted(@Param("title") String title, @Param("excludeId") UUID excludeId);
    
    /**
     * Find product by ID and not deleted
     */
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.isDeleted = false")
    Optional<Product> findByIdAndNotDeleted(@Param("id") UUID id);
    
    /**
     * Count products by category
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId AND p.isDeleted = false")
    long countByCategoryId(@Param("categoryId") UUID categoryId);
    
    /**
     * Find products with images
     */
    @Query("SELECT p FROM Product p WHERE SIZE(p.images) > 0 AND p.isDeleted = false ORDER BY p.title")
    List<Product> findProductsWithImages();
    
    /**
     * Find products with profile image
     */
    @Query("SELECT p FROM Product p WHERE p.profileImage IS NOT NULL AND p.profileImage != '' AND p.isDeleted = false ORDER BY p.title")
    List<Product> findProductsWithProfileImage();
}
