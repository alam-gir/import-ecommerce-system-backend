package com.importer_ecommerce.importEcommerce.cart.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CartItemResponse(
    UUID itemId,
    UUID variantId,
    UUID productId,
    String productTitle,
    String variantTitle,
    String productImage,
    BigDecimal unitPrice,
    BigDecimal compareAtPrice,
    Integer quantity,
    Integer minOrderQuantity,
    Integer availableStock,
    BigDecimal totalPrice,
    boolean hasDiscount,
    BigDecimal discountAmount,
    String discountPercentage,
    LocalDateTime addedAt,
    LocalDateTime updatedAt
) {}
