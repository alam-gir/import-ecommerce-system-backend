package com.importer_ecommerce.importEcommerce.product.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // HTML content from rich text editor
    
    @ElementCollection
    @CollectionTable(name = "product_media_descriptions", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "media_url", columnDefinition = "TEXT")
    private List<String> mediaDescriptions; // List of image URLs from cloud storage
    
    @Column(name = "note")
    private String note;
    
    @Column(name = "brand")
    private String brand;
    
    @Column(name = "attributes", columnDefinition = "TEXT")
    private String attributes; // JSON object for flexible fields
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.DRAFT;
    
    @Column(name = "featured")
    private Boolean featured = false;
    
    @Column(name = "seo_title")
    private String seoTitle;
    
    @Column(name = "seo_description")
    private String seoDescription;
    
    @Column(name = "seo_keywords")
    private String seoKeywords;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;
    
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
    
    public enum ProductStatus {
        DRAFT, PUBLISHED, ARCHIVED, DELETED
    }
}
