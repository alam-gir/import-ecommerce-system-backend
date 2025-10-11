package com.importer_ecommerce.importEcommerce.auth.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO for OTP operations
 */
public record OtpResponse(
    String message,
    LocalDateTime expiresAt
) {}
