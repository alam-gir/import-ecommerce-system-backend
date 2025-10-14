package com.importer_ecommerce.importEcommerce.modules.product.dto.response;

import com.importer_ecommerce.importEcommerce.modules.product.entity.CategoryStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Simple response DTO for category data - for basic operations
 */
public record CategorySimpleResponse(
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

