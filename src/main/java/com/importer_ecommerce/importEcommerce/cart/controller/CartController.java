package com.importer_ecommerce.importEcommerce.cart.controller;

import com.importer_ecommerce.importEcommerce.cart.dto.request.AddToCartRequest;
import com.importer_ecommerce.importEcommerce.cart.dto.request.RemoveFromCartRequest;
import com.importer_ecommerce.importEcommerce.cart.dto.request.UpdateCartItemRequest;
import com.importer_ecommerce.importEcommerce.cart.dto.response.CartResponse;
import com.importer_ecommerce.importEcommerce.cart.dto.response.CartSummaryResponse;
import com.importer_ecommerce.importEcommerce.cart.service.CartService;
import com.importer_ecommerce.importEcommerce.common.utils.ApiResponse;
import com.importer_ecommerce.importEcommerce.auth.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {
    
    private final CartService cartService;
    private final JwtUtils jwtUtils;
    
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            UUID customerId = extractCustomerIdFromRequest(httpRequest);
            log.info("Add to cart request for customer {}: variant {}, quantity {}", 
                customerId, request.getVariantId(), request.getQuantity());
            
            CartResponse cart = cartService.addToCart(request, customerId);
            
            return ResponseEntity.ok(ApiResponse.success(cart, 
                String.format("✅ %s added to cart successfully!", cart.lastAddedItemName())
            ));
            
        } catch (Exception e) {
            log.error("Error adding item to cart: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "CART_ERROR", "/api/cart/add"));
        }
    }
    
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @Valid @RequestBody UpdateCartItemRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            UUID customerId = extractCustomerIdFromRequest(httpRequest);
            log.info("Update cart item request for customer {}: item {}, quantity {}", 
                customerId, request.getItemId(), request.getQuantity());
            
            CartResponse cart = cartService.updateCartItem(request, customerId);
            
            return ResponseEntity.ok(ApiResponse.success(cart, 
                "✅ Cart item updated successfully!"
            ));
            
        } catch (Exception e) {
            log.error("Error updating cart item: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "CART_UPDATE_ERROR", "/api/cart/update"));
        }
    }
    
    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<CartResponse>> removeFromCart(
            @Valid @RequestBody RemoveFromCartRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            UUID customerId = extractCustomerIdFromRequest(httpRequest);
            log.info("Remove from cart request for customer {}: item {}", 
                customerId, request.getItemId());
            
            CartResponse cart = cartService.removeFromCart(request, customerId);
            
            return ResponseEntity.ok(ApiResponse.success(cart, 
                "✅ Item removed from cart successfully!"
            ));
            
        } catch (Exception e) {
            log.error("Error removing item from cart: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "CART_REMOVE_ERROR", "/api/cart/remove"));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(HttpServletRequest httpRequest) {
        try {
            UUID customerId = extractCustomerIdFromRequest(httpRequest);
            log.info("Get cart request for customer: {}", customerId);
            
            CartResponse cart = cartService.getCart(customerId);
            
            String message = cart.items().isEmpty() 
                ? "🛒 Your cart is empty. Add some products to get started!"
                : String.format("🛒 Your cart has %d items", cart.items().size());
            
            return ResponseEntity.ok(ApiResponse.success(cart, message));
            
        } catch (Exception e) {
            log.error("Error getting cart: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "CART_GET_ERROR", "/api/cart"));
        }
    }
    
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> getCartSummary(HttpServletRequest httpRequest) {
        try {
            UUID customerId = extractCustomerIdFromRequest(httpRequest);
            log.info("Get cart summary request for customer: {}", customerId);
            
            CartSummaryResponse summary = cartService.getCartSummary(customerId);
            
            return ResponseEntity.ok(ApiResponse.success(summary, summary.nextStepMessage()));
            
        } catch (Exception e) {
            log.error("Error getting cart summary: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "CART_SUMMARY_ERROR", "/api/cart/summary"));
        }
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(HttpServletRequest httpRequest) {
        try {
            UUID customerId = extractCustomerIdFromRequest(httpRequest);
            log.info("Clear cart request for customer: {}", customerId);
            
            cartService.clearCart(customerId);
            
            return ResponseEntity.ok(ApiResponse.success(null, "🛒 Cart cleared successfully!"));
            
        } catch (Exception e) {
            log.error("Error clearing cart: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "CART_CLEAR_ERROR", "/api/cart/clear"));
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getCartItemCount(HttpServletRequest httpRequest) {
        try {
            UUID customerId = extractCustomerIdFromRequest(httpRequest);
            
            Long itemCount = cartService.getCartItemCount(customerId);
            
            return ResponseEntity.ok(ApiResponse.success(itemCount, 
                String.format("🛒 Your cart has %d items", itemCount)));
            
        } catch (Exception e) {
            log.error("Error getting cart item count: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage(), "CART_COUNT_ERROR", "/api/cart/count"));
        }
    }
    
    // Helper method to extract customer ID from JWT token
    private UUID extractCustomerIdFromRequest(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null) {
            throw new RuntimeException("No authorization token found");
        }
        
        UUID customerId = jwtUtils.extractUserId(token);
        if (customerId == null) {
            throw new RuntimeException("Invalid authorization token");
        }
        
        return customerId;
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
