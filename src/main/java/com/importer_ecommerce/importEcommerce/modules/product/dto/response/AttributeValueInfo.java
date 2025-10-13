package com.importer_ecommerce.importEcommerce.modules.product.dto.response;

import java.util.UUID;

/**
 * Response DTO for attribute value information in variant context
 * Shows which attribute values are linked to a variant
 */
public record AttributeValueInfo(
    UUID attributeValueId,
    String attributeName,
    String value,
    String imageUrl
) {}

