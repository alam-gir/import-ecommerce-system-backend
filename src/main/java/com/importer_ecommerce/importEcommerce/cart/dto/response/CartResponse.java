package com.importer_ecommerce.importEcommerce.cart.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CartResponse(
    UUID cartId,
    List<CartItemResponse> items,
    CartSummaryResponse summary,
    String lastAddedItemName,
    LocalDateTime lastUpdated,
    boolean hasWarnings,
    List<String> warnings,
    boolean canCheckout
) {}
