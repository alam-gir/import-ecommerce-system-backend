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
    
    @Column(length = 500, columnDefinition = "TEXT")
    private String image;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CategoryStatus status = CategoryStatus.ACTIVE;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Category> children = new ArrayList<>();
    
    /**
     * Check if category is root (has no parent)
     */
    public boolean isRoot() {
        return parent == null;
    }
    
    /**
     * Check if category has children
     */
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }
    
    /**
     * Get category level (0 for root, 1 for first level, etc.)
     */
    public int getLevel() {
        int level = 0;
        Category current = parent;
        while (current != null) {
            level++;
            current = current.getParent();
        }
        return level;
    }
}
