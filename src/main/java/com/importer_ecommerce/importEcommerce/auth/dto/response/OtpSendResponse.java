package com.importer_ecommerce.importEcommerce.auth.dto.response;

import java.time.LocalDateTime;

public record OtpSendResponse(
    String message,
    LocalDateTime expiresAt
) {}
