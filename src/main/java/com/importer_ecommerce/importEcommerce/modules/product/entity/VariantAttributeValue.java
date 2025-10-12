package com.importer_ecommerce.importEcommerce.modules.product.entity;

import com.importer_ecommerce.importEcommerce.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * VariantAttributeValue entity - Specific values for each attribute (Red, Blue, Small, Large, etc.)
 */
@Entity
@Table(name = "variant_attribute_values")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantAttributeValue extends AuditableEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_attribute_id", nullable = false)
    private VariantAttribute variantAttribute;
    
    @Column(name = "value", nullable = false, length = 255)
    private String value; // e.g., "Red", "Blue", "Small", "Large"
    
    @Column(name = "image_url", length = 500)
    private String imageUrl; // For image-based attributes like color swatches
    
    // Many-to-many relationship with ProductVariant
    @ManyToMany(mappedBy = "attributeValues")
    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>();
    
    /**
     * Add variant
     */
    public void addVariant(ProductVariant variant) {
        if (variants == null) {
            variants = new ArrayList<>();
        }
        variants.add(variant);
    }
    
    /**
     * Remove variant
     */
    public void removeVariant(ProductVariant variant) {
        if (variants != null) {
            variants.remove(variant);
        }
    }
    
    /**
     * Check if this value has an image
     */
    public boolean hasImage() {
        return imageUrl != null && !imageUrl.isEmpty();
    }
}