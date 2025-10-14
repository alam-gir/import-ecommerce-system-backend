package com.importer_ecommerce.importEcommerce.modules.product.dto.response;

import com.importer_ecommerce.importEcommerce.modules.product.entity.CategoryStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for category data - optimized for admin panel
 */
public record CategoryResponse(
    UUID id,
    String title,
    String image,
    String description,
    CategoryStatus status,
    UUID parentId,
    String parentTitle,
    List<CategoryResponse> children,
    int level,
    boolean hasChildren,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    // Constructor for categories without children (for performance)
    public CategoryResponse(UUID id, String title, String image, String description, 
                          CategoryStatus status, UUID parentId, String parentTitle, 
                          int level, boolean hasChildren, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, title, image, description, status, parentId, parentTitle, null, level, hasChildren, createdAt, updatedAt);
    }
}
