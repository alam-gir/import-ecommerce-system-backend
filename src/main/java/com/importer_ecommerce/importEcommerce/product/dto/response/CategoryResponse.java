package com.importer_ecommerce.importEcommerce.product.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CategoryResponse(
    UUID id,
    String title,
    String description,
    String profilePicture,
    String coverImage,
    UUID parentId,
    String parentTitle,
    List<CategoryResponse> children,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
