package com.importer_ecommerce.importEcommerce.cart.entity;

import com.importer_ecommerce.importEcommerce.product.entity.ProductVariant;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "min_order_quantity", nullable = false)
    private Integer minOrderQuantity; // Stored for validation
    
    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice; // Stored for price calculations
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper methods for cart item management
    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    public boolean isQuantityValid() {
        return quantity >= minOrderQuantity;
    }
    
    public boolean hasStockAvailable(int availableStock) {
        return quantity <= availableStock;
    }
    
    public String getProductTitle() {
        return productVariant.getProduct().getTitle();
    }
    
    public String getVariantTitle() {
        return productVariant.getTitle();
    }
    
    public String getMainProductImage() {
        List<String> productImages = productVariant.getProduct().getMediaDescriptions();
        if (productImages != null && !productImages.isEmpty()) {
            return productImages.get(0);
        }
        return null;
    }
}
