package com.importer_ecommerce.importEcommerce.modules.product.dto.response;

import com.importer_ecommerce.importEcommerce.modules.product.entity.CategoryStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for hierarchical category data - without children field
 */
public record CategoryHierarchicalResponse(
    UUID id,
    String title,
    String image,
    String description,
    CategoryStatus status,
    UUID parentId,
    String parentTitle,
    int level,
    boolean hasChildren,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

