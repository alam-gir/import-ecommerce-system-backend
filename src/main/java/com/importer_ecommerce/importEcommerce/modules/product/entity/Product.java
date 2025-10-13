package com.importer_ecommerce.importEcommerce.modules.product.entity;

import com.importer_ecommerce.importEcommerce.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Product entity for e-commerce products
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends AuditableEntity {
    
    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    @Column(length = 500)
    private String profileImage;
    
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url", length = 500)
    @Builder.Default
    private List<String> images = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "product_description_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "description_image_url", length = 500)
    @Builder.Default
    private List<String> descriptionImages = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Column(name = "minimum_order_quantity")
    private Integer minimumOrderQuantity;
    
    // Product relationships
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>();
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<VariantAttribute> variantAttributes = new ArrayList<>();
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductSpecification> specifications = new ArrayList<>();
    
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProductSEO seo;
    
    /**
     * Check if product has minimum order quantity requirement
     */
    public boolean hasMinimumOrderQuantity() {
        return minimumOrderQuantity != null && minimumOrderQuantity > 0;
    }
    
    /**
     * Get minimum order quantity or default to 1
     */
    public Integer getEffectiveMinimumQuantity() {
        return hasMinimumOrderQuantity() ? minimumOrderQuantity : 1;
    }
    
    /**
     * Add image to product
     */
    public void addImage(String imageUrl) {
        if (images == null) {
            images = new ArrayList<>();
        }
        images.add(imageUrl);
    }
    
    /**
     * Remove image from product
     */
    public void removeImage(String imageUrl) {
        if (images != null) {
            images.remove(imageUrl);
        }
    }
    
    /**
     * Add description image to product
     */
    public void addDescriptionImage(String imageUrl) {
        if (descriptionImages == null) {
            descriptionImages = new ArrayList<>();
        }
        descriptionImages.add(imageUrl);
    }
    
    /**
     * Remove description image from product
     */
    public void removeDescriptionImage(String imageUrl) {
        if (descriptionImages != null) {
            descriptionImages.remove(imageUrl);
        }
    }
}
