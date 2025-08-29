package com.importer_ecommerce.importEcommerce.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RemoveFromCartRequest {
    
    @NotNull(message = "Cart item ID is required")
    private UUID itemId;
}
