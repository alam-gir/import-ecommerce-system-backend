package com.importer_ecommerce.importEcommerce.modules.product.entity;

import com.importer_ecommerce.importEcommerce.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * VariantAttribute entity - Defines attribute types per product (Color, Size, Material, etc.)
 */
@Entity
@Table(name = "variant_attributes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantAttribute extends AuditableEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name; // e.g., "Color", "Size", "Material"
    
    @Enumerated(EnumType.STRING)
    @Column(name = "attribute_type", nullable = false)
    private AttributeType attributeType; // TEXT, IMAGE, NUMBER
    
    // One-to-many relationship with VariantAttributeValue
    @OneToMany(mappedBy = "variantAttribute", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<VariantAttributeValue> attributeValues = new ArrayList<>();
    
    /**
     * Add attribute value
     */
    public void addAttributeValue(VariantAttributeValue value) {
        if (attributeValues == null) {
            attributeValues = new ArrayList<>();
        }
        attributeValues.add(value);
        value.setVariantAttribute(this);
    }
    
    /**
     * Remove attribute value
     */
    public void removeAttributeValue(VariantAttributeValue value) {
        if (attributeValues != null) {
            attributeValues.remove(value);
            value.setVariantAttribute(null);
        }
    }
    
    /**
     * Attribute type enum
     */
    public enum AttributeType {
        TEXT,    // Text values like "Red", "Blue", "Small", "Large"
        IMAGE,   // Image-based attributes like color swatches
        NUMBER   // Numeric values like "10", "20", "30"
    }
}
