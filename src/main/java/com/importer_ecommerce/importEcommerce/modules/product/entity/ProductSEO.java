package com.importer_ecommerce.importEcommerce.modules.product.entity;

import com.importer_ecommerce.importEcommerce.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * ProductSEO entity - SEO metadata for products
 */
@Entity
@Table(name = "product_seo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSEO extends AuditableEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "meta_title", length = 255)
    private String metaTitle;
    
    @Column(name = "meta_description", length = 500)
    private String metaDescription;
    
    @Column(name = "meta_keywords", length = 500)
    private String metaKeywords;
    
    @Column(name = "meta_image", length = 500)
    private String metaImage;
    
    @Column(name = "canonical_url", length = 500)
    private String canonicalUrl;
    
    @Column(name = "og_title", length = 255)
    private String ogTitle;
    
    @Column(name = "og_description", length = 500)
    private String ogDescription;
    
    @Column(name = "og_image", length = 500)
    private String ogImage;
    
    @Column(name = "twitter_title", length = 255)
    private String twitterTitle;
    
    @Column(name = "twitter_description", length = 500)
    private String twitterDescription;
    
    @Column(name = "twitter_image", length = 500)
    private String twitterImage;
    
    @Column(name = "structured_data", columnDefinition = "TEXT")
    private String structuredData; // JSON-LD structured data
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    /**
     * Get effective meta title (fallback to product title)
     */
    public String getEffectiveMetaTitle() {
        return metaTitle != null && !metaTitle.isEmpty() ? metaTitle : 
               (product != null ? product.getTitle() : null);
    }
    
    /**
     * Get effective meta description
     */
    public String getEffectiveMetaDescription() {
        return metaDescription != null && !metaDescription.isEmpty() ? metaDescription : null;
    }
    
    /**
     * Get effective OG title (fallback to meta title)
     */
    public String getEffectiveOgTitle() {
        return ogTitle != null && !ogTitle.isEmpty() ? ogTitle : getEffectiveMetaTitle();
    }
    
    /**
     * Get effective OG description (fallback to meta description)
     */
    public String getEffectiveOgDescription() {
        return ogDescription != null && !ogDescription.isEmpty() ? ogDescription : getEffectiveMetaDescription();
    }
    
    /**
     * Get effective Twitter title (fallback to OG title)
     */
    public String getEffectiveTwitterTitle() {
        return twitterTitle != null && !twitterTitle.isEmpty() ? twitterTitle : getEffectiveOgTitle();
    }
    
    /**
     * Get effective Twitter description (fallback to OG description)
     */
    public String getEffectiveTwitterDescription() {
        return twitterDescription != null && !twitterDescription.isEmpty() ? twitterDescription : getEffectiveOgDescription();
    }
}
