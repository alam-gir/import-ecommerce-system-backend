package com.importer_ecommerce.importEcommerce.product.dto.response;

import java.util.List;

public record ProductVariantListResponse(
    List<ProductVariantResponse> variants,
    long totalElements,
    int totalPages,
    int currentPage,
    int pageSize
) {}
