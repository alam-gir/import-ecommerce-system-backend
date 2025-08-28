package com.importer_ecommerce.importEcommerce.product.dto.response;

import java.util.List;

public record ProductListResponse(
    List<ProductResponse> products,
    long totalElements,
    int totalPages,
    int currentPage,
    int pageSize
) {}
