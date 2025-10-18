package com.importer_ecommerce.importEcommerce.modules.product.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for product data
 * Contains all product information including relationships
 */
public record ProductResponse(
    UUID id,
    String title,
    String slug,
    String description,
    String profileImage,
    List<String> images,
    List<String> descriptionImages,
    Integer minimumOrderQuantity,
    String status,
    CategoryInfo category,
    List<ProductDetailVariantResponse> variants,
    List<ProductSpecificationResponse> specifications,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
