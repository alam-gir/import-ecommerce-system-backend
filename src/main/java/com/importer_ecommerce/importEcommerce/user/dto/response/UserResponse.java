package com.importer_ecommerce.importEcommerce.user.dto.response;

import com.importer_ecommerce.importEcommerce.user.entity.UserRole;
import com.importer_ecommerce.importEcommerce.user.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for user information
 */
public record UserResponse(
    UUID id,
    String name,
    String email,
    String phone,
    String profileImage,
    UserRole role,
    UserStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
