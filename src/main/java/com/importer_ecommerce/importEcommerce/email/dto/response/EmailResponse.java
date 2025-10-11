package com.importer_ecommerce.importEcommerce.email.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO for email operations
 */
public record EmailResponse(
    boolean success,
    String message,
    LocalDateTime sentAt
) {}
