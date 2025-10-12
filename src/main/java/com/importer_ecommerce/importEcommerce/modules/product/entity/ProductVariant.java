package com.importer_ecommerce.importEcommerce.modules.product.entity;

import com.importer_ecommerce.importEcommerce.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductVariant entity - Actual sellable SKUs with price and stock
 * Each variant is a unique combination of attribute values
 */
@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant extends AuditableEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "sku", nullable = false, unique = true, length = 100)
    private String sku;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "compare_at_price", precision = 10, scale = 2)
    private BigDecimal compareAtPrice;
    
    @Column(name = "cost_price", precision = 10, scale = 2)
    private BigDecimal costPrice;
    
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;
    
    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold;
    
    @Column(name = "weight", precision = 8, scale = 3)
    private BigDecimal weight;
    
    @Column(name = "dimensions", length = 100)
    private String dimensions; // Format: "LxWxH" in cm
    
    @Column(name = "barcode", length = 50)
    private String barcode;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "is_tracked", nullable = false)
    @Builder.Default
    private Boolean isTracked = true; // Track inventory
    
    // Many-to-many relationship with VariantAttributeValue
    @ManyToMany
    @JoinTable(
        name = "variant_attribute_value_links",
        joinColumns = @JoinColumn(name = "product_variant_id"),
        inverseJoinColumns = @JoinColumn(name = "variant_attribute_value_id")
    )
    @Builder.Default
    private List<VariantAttributeValue> attributeValues = new ArrayList<>();
    
    /**
     * Check if variant is in stock
     */
    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }
    
    /**
     * Check if variant is low in stock
     */
    public boolean isLowStock() {
        return lowStockThreshold != null && stockQuantity != null && stockQuantity <= lowStockThreshold;
    }
    
    /**
     * Check if variant has discount (compare at price > price)
     */
    public boolean hasDiscount() {
        return compareAtPrice != null && price != null && compareAtPrice.compareTo(price) > 0;
    }
    
    /**
     * Get discount percentage
     */
    public BigDecimal getDiscountPercentage() {
        if (!hasDiscount()) {
            return BigDecimal.ZERO;
        }
        BigDecimal discount = compareAtPrice.subtract(price);
        return discount.divide(compareAtPrice, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
    
    /**
     * Add attribute value
     */
    public void addAttributeValue(VariantAttributeValue value) {
        if (attributeValues == null) {
            attributeValues = new ArrayList<>();
        }
        attributeValues.add(value);
    }
    
    /**
     * Remove attribute value
     */
    public void removeAttributeValue(VariantAttributeValue value) {
        if (attributeValues != null) {
            attributeValues.remove(value);
        }
    }
    
    /**
     * Get attribute value by attribute name
     */
    public String getAttributeValueByName(String attributeName) {
        return attributeValues.stream()
            .filter(value -> value.getVariantAttribute().getName().equals(attributeName))
            .map(VariantAttributeValue::getValue)
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Get all attribute names and values as a map-like structure
     */
    public List<AttributeInfo> getAttributeInfo() {
        return attributeValues.stream()
            .map(value -> new AttributeInfo(
                value.getVariantAttribute().getName(),
                value.getValue(),
                value.getImageUrl()
            ))
            .toList();
    }
    
    /**
     * Helper record for attribute information
     */
    public record AttributeInfo(String attributeName, String value, String imageUrl) {}
}
