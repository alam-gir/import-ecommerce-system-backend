package com.importer_ecommerce.importEcommerce.cart.service;

import com.importer_ecommerce.importEcommerce.cart.dto.request.AddToCartRequest;
import com.importer_ecommerce.importEcommerce.cart.dto.request.RemoveFromCartRequest;
import com.importer_ecommerce.importEcommerce.cart.dto.request.UpdateCartItemRequest;
import com.importer_ecommerce.importEcommerce.cart.dto.response.CartResponse;
import com.importer_ecommerce.importEcommerce.cart.dto.response.CartSummaryResponse;

import java.util.UUID;

public interface CartService {
    
    CartResponse addToCart(AddToCartRequest request, UUID customerId);
    
    CartResponse updateCartItem(UpdateCartItemRequest request, UUID customerId);
    
    CartResponse removeFromCart(RemoveFromCartRequest request, UUID customerId);
    
    CartResponse getCart(UUID customerId);
    
    CartSummaryResponse getCartSummary(UUID customerId);
    
    void clearCart(UUID customerId);
    
    boolean isItemAlreadyInCart(UUID variantId, UUID customerId);
    
    Long getCartItemCount(UUID customerId);
}
