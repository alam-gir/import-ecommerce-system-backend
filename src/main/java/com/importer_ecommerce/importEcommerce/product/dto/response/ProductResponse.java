package com.importer_ecommerce.importEcommerce.product.dto.response;

import com.importer_ecommerce.importEcommerce.product.entity.Product;
import static com.importer_ecommerce.importEcommerce.product.entity.Product.ProductStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
    UUID id,
    String title,
    String slug,
    String description,
    List<String> mediaDescriptions,
    String note,
    String brand,
    Integer minOrderQuantity,
    String attributes,
    ProductStatus status,
    Boolean featured,
    String seoTitle,
    String seoDescription,
    String seoKeywords,
    List<ProductCategoryResponse> categories,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
