package com.importer_ecommerce.importEcommerce.cart.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CartSummaryResponse(
    BigDecimal subtotal,
    int totalItems,
    int itemCount,
    List<String> warnings,
    boolean canProceedToCheckout,
    String nextStepMessage
) {}
