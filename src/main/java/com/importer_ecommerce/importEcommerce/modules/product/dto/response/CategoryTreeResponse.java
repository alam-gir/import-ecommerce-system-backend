package com.importer_ecommerce.importEcommerce.modules.product.dto.response;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO for category tree structure (hierarchical with children)
 */
public record CategoryTreeResponse(
    UUID id,
    String title,
    List<CategoryTreeResponse> children
) {}
