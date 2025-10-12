package com.importer_ecommerce.importEcommerce.modules.product.dto.response;

import com.importer_ecommerce.importEcommerce.modules.product.entity.CategoryStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for category data
 */
public record CategoryResponse(
    UUID id,
    String title,
    String image,
    String description,
    CategoryStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
