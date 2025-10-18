package com.importer_ecommerce.importEcommerce.modules.product.dto.response;

import java.util.UUID;

/**
 * Response DTO for detailed attribute value information
 * Contains complete attribute and value details for variant responses
 */
public record VariantAttributeDetailResponse(
    UUID attributeId,
    String attributeName,
    String attributeType,
    UUID valueId,
    String value,
    String imageUrl
) {}
