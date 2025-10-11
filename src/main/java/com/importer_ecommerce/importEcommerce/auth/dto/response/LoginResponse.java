package com.importer_ecommerce.importEcommerce.auth.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO for login success
 */
public record LoginResponse(
    String accessToken,
    String refreshToken,
    ProfileResponse user,
    LocalDateTime expiresAt
) {}
