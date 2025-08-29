package com.importer_ecommerce.importEcommerce.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateCartItemRequest {
    
    @NotNull(message = "Cart item ID is required")
    private UUID itemId;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}
