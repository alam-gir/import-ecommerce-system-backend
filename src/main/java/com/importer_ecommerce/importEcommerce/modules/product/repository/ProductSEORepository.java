package com.importer_ecommerce.importEcommerce.modules.product.repository;

import com.importer_ecommerce.importEcommerce.modules.product.entity.ProductSEO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ProductSEO entity
 */
@Repository
public interface ProductSEORepository extends JpaRepository<ProductSEO, UUID> {
    
    /**
     * Find SEO by product ID
     */
    @Query("SELECT s FROM ProductSEO s WHERE s.product.id = :productId AND s.isDeleted = false")
    Optional<ProductSEO> findByProductId(@Param("productId") UUID productId);
    
    /**
     * Find active SEO by product ID
     */
    @Query("SELECT s FROM ProductSEO s WHERE s.product.id = :productId AND s.isActive = true AND s.isDeleted = false")
    Optional<ProductSEO> findActiveByProductId(@Param("productId") UUID productId);
    
    /**
     * Find SEO with meta titles
     */
    @Query("SELECT s FROM ProductSEO s WHERE s.metaTitle IS NOT NULL AND s.metaTitle != '' AND s.isDeleted = false ORDER BY s.metaTitle")
    List<ProductSEO> findWithMetaTitles();
    
    /**
     * Find SEO with meta descriptions
     */
    @Query("SELECT s FROM ProductSEO s WHERE s.metaDescription IS NOT NULL AND s.metaDescription != '' AND s.isDeleted = false ORDER BY s.metaTitle")
    List<ProductSEO> findWithMetaDescriptions();
    
    /**
     * Find SEO with meta keywords
     */
    @Query("SELECT s FROM ProductSEO s WHERE s.metaKeywords IS NOT NULL AND s.metaKeywords != '' AND s.isDeleted = false ORDER BY s.metaTitle")
    List<ProductSEO> findWithMetaKeywords();
    
    /**
     * Find SEO with meta images
     */
    @Query("SELECT s FROM ProductSEO s WHERE s.metaImage IS NOT NULL AND s.metaImage != '' AND s.isDeleted = false ORDER BY s.metaTitle")
    List<ProductSEO> findWithMetaImages();
    
    /**
     * Find SEO with canonical URLs
     */
    @Query("SELECT s FROM ProductSEO s WHERE s.canonicalUrl IS NOT NULL AND s.canonicalUrl != '' AND s.isDeleted = false ORDER BY s.canonicalUrl")
    List<ProductSEO> findWithCanonicalUrls();
    
    /**
     * Find SEO with structured data
     */
    @Query("SELECT s FROM ProductSEO s WHERE s.structuredData IS NOT NULL AND s.structuredData != '' AND s.isDeleted = false ORDER BY s.metaTitle")
    List<ProductSEO> findWithStructuredData();
    
    /**
     * Find SEO by meta title pattern
     */
    @Query("SELECT s FROM ProductSEO s WHERE s.metaTitle LIKE %:titlePattern% AND s.isDeleted = false ORDER BY s.metaTitle")
    List<ProductSEO> findByMetaTitleContaining(@Param("titlePattern") String titlePattern);
    
    /**
     * Find SEO by meta description pattern
     */
    @Query("SELECT s FROM ProductSEO s WHERE s.metaDescription LIKE %:descriptionPattern% AND s.isDeleted = false ORDER BY s.metaTitle")
    List<ProductSEO> findByMetaDescriptionContaining(@Param("descriptionPattern") String descriptionPattern);
    
    /**
     * Find SEO by meta keywords pattern
     */
    @Query("SELECT s FROM ProductSEO s WHERE s.metaKeywords LIKE %:keywordsPattern% AND s.isDeleted = false ORDER BY s.metaTitle")
    List<ProductSEO> findByMetaKeywordsContaining(@Param("keywordsPattern") String keywordsPattern);
    
    /**
     * Find SEO by canonical URL pattern
     */
    @Query("SELECT s FROM ProductSEO s WHERE s.canonicalUrl LIKE %:urlPattern% AND s.isDeleted = false ORDER BY s.canonicalUrl")
    List<ProductSEO> findByCanonicalUrlContaining(@Param("urlPattern") String urlPattern);
    
    /**
     * Find active SEO
     */
    @Query("SELECT s FROM ProductSEO s WHERE s.isActive = true AND s.isDeleted = false ORDER BY s.metaTitle")
    List<ProductSEO> findActive();
    
    /**
     * Find inactive SEO
     */
    @Query("SELECT s FROM ProductSEO s WHERE s.isActive = false AND s.isDeleted = false ORDER BY s.metaTitle")
    List<ProductSEO> findInactive();
    
    /**
     * Check if SEO exists for product
     */
    @Query("SELECT COUNT(s) > 0 FROM ProductSEO s WHERE s.product.id = :productId AND s.isDeleted = false")
    boolean existsByProductId(@Param("productId") UUID productId);
    
    /**
     * Count SEO records
     */
    @Query("SELECT COUNT(s) FROM ProductSEO s WHERE s.isDeleted = false")
    long countAll();
    
    /**
     * Count active SEO records
     */
    @Query("SELECT COUNT(s) FROM ProductSEO s WHERE s.isActive = true AND s.isDeleted = false")
    long countActive();
    
    /**
     * Find SEO without meta titles
     */
    @Query("SELECT s FROM ProductSEO s WHERE (s.metaTitle IS NULL OR s.metaTitle = '') AND s.isDeleted = false ORDER BY s.product.title")
    List<ProductSEO> findWithoutMetaTitles();
    
    /**
     * Find SEO without meta descriptions
     */
    @Query("SELECT s FROM ProductSEO s WHERE (s.metaDescription IS NULL OR s.metaDescription = '') AND s.isDeleted = false ORDER BY s.product.title")
    List<ProductSEO> findWithoutMetaDescriptions();
}
