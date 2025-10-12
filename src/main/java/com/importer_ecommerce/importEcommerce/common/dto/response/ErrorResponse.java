package com.importer_ecommerce.importEcommerce.common.dto.response;

/**
 * Error response structure for API responses
 */
public record ErrorResponse(
    String code,
    String message,
    String field,
    Object rejectedValue
) {}

