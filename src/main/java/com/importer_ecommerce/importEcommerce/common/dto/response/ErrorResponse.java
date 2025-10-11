package com.importer_ecommerce.importEcommerce.common.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Error response structure for API responses
 */
public record ErrorResponse(
    String code,
    String message,
    String field,
    Object rejectedValue
) {}

