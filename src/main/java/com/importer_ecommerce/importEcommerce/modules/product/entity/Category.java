package com.importer_ecommerce.importEcommerce.modules.product.entity;

import com.importer_ecommerce.importEcommerce.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Category entity for product categorization
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends AuditableEntity {
    
    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(length = 500)
    private String image;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CategoryStatus status = CategoryStatus.ACTIVE;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Product> products = new ArrayList<>();
    
    /**
     * Add product to category
     */
    public void addProduct(Product product) {
        products.add(product);
        product.setCategory(this);
    }
    
    /**
     * Remove product from category
     */
    public void removeProduct(Product product) {
        products.remove(product);
        product.setCategory(null);
    }
}
