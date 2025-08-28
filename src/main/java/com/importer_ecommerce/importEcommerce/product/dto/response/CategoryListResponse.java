package com.importer_ecommerce.importEcommerce.product.dto.response;

import java.util.List;

public record CategoryListResponse(
    List<CategoryResponse> categories,
    int totalCount
) {}
