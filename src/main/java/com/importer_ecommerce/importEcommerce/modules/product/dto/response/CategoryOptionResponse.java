package com.importer_ecommerce.importEcommerce.modules.product.dto.response;

import java.util.UUID;

/**
 * Response DTO for category options - minimal data for UI select dropdowns
 */
public record CategoryOptionResponse(
    UUID id,
    String title
) {}
