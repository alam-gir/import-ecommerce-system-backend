package com.importer_ecommerce.importEcommerce.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class AddToCartRequest {
    
    @NotNull(message = "Product variant ID is required")
    private UUID variantId;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}
