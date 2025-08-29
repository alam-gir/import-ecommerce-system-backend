package com.importer_ecommerce.importEcommerce.cart.dto.validation;

public record ValidationError(
    String field,
    String message,
    String suggestion,
    String errorCode
) {}
