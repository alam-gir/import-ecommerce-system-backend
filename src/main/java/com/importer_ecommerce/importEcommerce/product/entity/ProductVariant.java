package com.importer_ecommerce.importEcommerce.product.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "sku", unique = true)
    private String sku;
    
    @Column(name = "title")
    private String title; // e.g., "Red, Size 10"
    
    @Column(name = "attributes", columnDefinition = "TEXT")
    private String attributes; // JSON object for variant attributes like color, size, etc.
    
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "compare_at_price", precision = 10, scale = 2)
    private BigDecimal compareAtPrice; // Original price for discounts
    
    @Column(name = "cost_price", precision = 10, scale = 2)
    private BigDecimal costPrice; // Wholesale/manufacturing cost
    
    @Column(name = "weight")
    private Double weight;
    
    @Column(name = "dimensions", columnDefinition = "TEXT")
    private String dimensions; // JSON: {"length": 10, "width": 5, "height": 2}
    
    @Column(name = "inventory_quantity", nullable = false)
    private Integer inventoryQuantity = 0;
    
    @Column(name = "inventory_tracking")
    @Enumerated(EnumType.STRING)
    private InventoryTracking inventoryTracking = InventoryTracking.TRACKED;
    
    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold = 5;
    
    @ElementCollection
    @CollectionTable(name = "product_variant_images", joinColumns = @JoinColumn(name = "variant_id"))
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
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
    
    public enum InventoryTracking {
        TRACKED, NOT_TRACKED, TRACKED_LOW_STOCK
    }
}
