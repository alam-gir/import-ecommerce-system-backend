package com.importer_ecommerce.importEcommerce.product.dto.response;

import java.util.UUID;

public record ProductCategoryResponse(
    UUID id,
    String title,
    String description,
    String profilePicture,
    String coverImage
) {}
