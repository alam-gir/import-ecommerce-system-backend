package com.importer_ecommerce.importEcommerce.modules.customers.dto.response;

import com.importer_ecommerce.importEcommerce.user.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerResponse(
    UUID id,
    String phone,
    String name,
    String email,
    String profileImage,
    UserStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
