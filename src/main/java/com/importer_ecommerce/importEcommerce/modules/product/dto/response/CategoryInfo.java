package com.importer_ecommerce.importEcommerce.modules.product.dto.response;

import java.util.UUID;

/**
 * Response DTO for category information in product context
 */
public record CategoryInfo(
    UUID id,
    String title,
    String image
) {}

