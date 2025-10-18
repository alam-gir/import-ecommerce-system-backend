package com.importer_ecommerce.importEcommerce.modules.product.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductDetailVariantResponse(
    UUID id,
    String sku,
    BigDecimal price,
    BigDecimal compareAtPrice,
    Integer stockQuantity,
    Boolean isActive,
    List<VariantAttributeDetailResponse> attributeValues
) {}