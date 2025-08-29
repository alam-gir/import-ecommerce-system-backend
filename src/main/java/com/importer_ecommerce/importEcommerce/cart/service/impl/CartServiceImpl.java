package com.importer_ecommerce.importEcommerce.cart.service.impl;

import com.importer_ecommerce.importEcommerce.auth.entity.User;
import com.importer_ecommerce.importEcommerce.cart.dto.request.AddToCartRequest;
import com.importer_ecommerce.importEcommerce.cart.dto.request.RemoveFromCartRequest;
import com.importer_ecommerce.importEcommerce.cart.dto.request.UpdateCartItemRequest;
import com.importer_ecommerce.importEcommerce.cart.dto.response.CartResponse;
import com.importer_ecommerce.importEcommerce.cart.dto.response.CartSummaryResponse;
import com.importer_ecommerce.importEcommerce.cart.dto.validation.CartValidationResult;
import com.importer_ecommerce.importEcommerce.cart.entity.Cart;
import com.importer_ecommerce.importEcommerce.cart.entity.CartItem;
import com.importer_ecommerce.importEcommerce.cart.repository.CartItemRepository;
import com.importer_ecommerce.importEcommerce.cart.repository.CartRepository;
import com.importer_ecommerce.importEcommerce.cart.service.CartCalculationService;
import com.importer_ecommerce.importEcommerce.cart.service.CartMapperService;
import com.importer_ecommerce.importEcommerce.cart.service.CartService;
import com.importer_ecommerce.importEcommerce.cart.service.CartValidationService;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.product.entity.ProductVariant;
import com.importer_ecommerce.importEcommerce.product.repository.ProductVariantRepository;
import com.importer_ecommerce.importEcommerce.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements CartService {
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;
    private final CartValidationService cartValidationService;
    private final CartCalculationService cartCalculationService;
    private final CartMapperService cartMapperService;
    
    @Override
    public CartResponse addToCart(AddToCartRequest request, UUID customerId) {
        // 1. Validate the request
        CartValidationResult validation = cartValidationService.validateAddToCart(request, customerId);
        if (!validation.isValid()) {
            throw new RuntimeException("Cart validation failed: " + validation.userFriendlyMessage());
        }
        
        // 2. Get or create cart for customer
        Cart cart = getOrCreateCart(customerId);
        
        // 3. Get product variant
        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
            .orElseThrow(() -> new NotFoundException("Product Variant", request.getVariantId().toString()));
        
        // 4. Check if item already exists in cart
        CartItem existingItem = cartItemRepository.findByCartIdAndProductVariantId(cart.getId(), request.getVariantId()).orElse(null);
        
        if (existingItem != null) {
            // Update existing item quantity
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            existingItem.setQuantity(newQuantity);
            existingItem = cartItemRepository.save(existingItem);
            
            log.info("Updated cart item quantity for customer {}: variant {}, new quantity: {}", 
                customerId, request.getVariantId(), newQuantity);
            
            return cartMapperService.toCartResponse(cart, variant.getProduct().getTitle());
        } else {
            // Create new cart item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductVariant(variant);
            newItem.setQuantity(request.getQuantity());
            newItem.setMinOrderQuantity(variant.getProduct().getMinOrderQuantity());
            newItem.setUnitPrice(variant.getPrice());
            
            CartItem savedItem = cartItemRepository.save(newItem);
            cart.addItem(savedItem);
            
            log.info("Added new item to cart for customer {}: variant {}, quantity: {}", 
                customerId, request.getVariantId(), request.getQuantity());
            
            return cartMapperService.toCartResponse(cart, variant.getProduct().getTitle());
        }
    }
    
    @Override
    public CartResponse updateCartItem(UpdateCartItemRequest request, UUID customerId) {
        // 1. Get cart item and validate ownership
        CartItem cartItem = getCartItemWithOwnershipValidation(request.getItemId(), customerId);
        
        // 2. Validate new quantity
        if (request.getQuantity() < cartItem.getMinOrderQuantity()) {
            throw new RuntimeException(String.format(
                "Quantity %d is below minimum order quantity %d for product '%s'",
                request.getQuantity(),
                cartItem.getMinOrderQuantity(),
                cartItem.getProductTitle()
            ));
        }
        
        // 3. Check inventory
        if (request.getQuantity() > cartItem.getProductVariant().getInventoryQuantity()) {
            throw new RuntimeException(String.format(
                "Requested quantity %d exceeds available stock %d for product '%s'",
                request.getQuantity(),
                cartItem.getProductVariant().getInventoryQuantity(),
                cartItem.getProductTitle()
            ));
        }
        
        // 4. Update quantity
        cartItem.setQuantity(request.getQuantity());
        CartItem updatedItem = cartItemRepository.save(cartItem);
        
        log.info("Updated cart item quantity for customer {}: item {}, new quantity: {}", 
            customerId, request.getItemId(), request.getQuantity());
        
        Cart cart = cartRepository.findByCustomerIdWithItems(customerId)
            .orElseThrow(() -> new NotFoundException("Cart", customerId.toString()));
        
        return cartMapperService.toCartResponse(cart, updatedItem.getProductTitle());
    }
    
    @Override
    public CartResponse removeFromCart(RemoveFromCartRequest request, UUID customerId) {
        // 1. Get cart item and validate ownership
        CartItem cartItem = getCartItemWithOwnershipValidation(request.getItemId(), customerId);
        
        // 2. Remove item
        String productTitle = cartItem.getProductTitle();
        cartItemRepository.delete(cartItem);
        
        log.info("Removed item from cart for customer {}: item {}, product: {}", 
            customerId, request.getItemId(), productTitle);
        
        // 3. Get updated cart
        Cart cart = cartRepository.findByCustomerIdWithItems(customerId)
            .orElseThrow(() -> new NotFoundException("Cart", customerId.toString()));
        
        return cartMapperService.toCartResponse(cart, productTitle + " removed");
    }
    
    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(UUID customerId) {
        Cart cart = cartRepository.findByCustomerIdWithItems(customerId)
            .orElseThrow(() -> new NotFoundException("Cart", customerId.toString()));
        
        return cartMapperService.toCartResponse(cart, null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CartSummaryResponse getCartSummary(UUID customerId) {
        Cart cart = cartRepository.findByCustomerIdWithItems(customerId)
            .orElseThrow(() -> new NotFoundException("Cart", customerId.toString()));
        
        return cartMapperService.toCartSummaryResponse(cart);
    }
    
    @Override
    public void clearCart(UUID customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
            .orElseThrow(() -> new NotFoundException("Cart", customerId.toString()));
        
        cartItemRepository.deleteByCartId(cart.getId());
        log.info("Cleared cart for customer: {}", customerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isItemAlreadyInCart(UUID variantId, UUID customerId) {
        return cartItemRepository.existsByCartIdAndProductVariantId(
            getCartId(customerId), variantId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getCartItemCount(UUID customerId) {
        return cartRepository.getItemCountByCustomerId(customerId);
    }
    
    // Helper methods
    private Cart getOrCreateCart(UUID customerId) {
        return cartRepository.findByCustomerId(customerId)
            .orElseGet(() -> {
                User customer = userRepository.findById(customerId)
                    .orElseThrow(() -> new NotFoundException("User", customerId.toString()));
                
                Cart newCart = new Cart();
                newCart.setCustomer(customer);
                
                Cart savedCart = cartRepository.save(newCart);
                log.info("Created new cart for customer: {}", customerId);
                
                return savedCart;
            });
    }
    
    private CartItem getCartItemWithOwnershipValidation(UUID itemId, UUID customerId) {
        CartItem cartItem = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException("Cart Item", itemId.toString()));
        
        if (!cartItem.getCart().getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Access denied: Cart item does not belong to customer");
        }
        
        return cartItem;
    }
    
    private UUID getCartId(UUID customerId) {
        return cartRepository.findByCustomerId(customerId)
            .map(Cart::getId)
            .orElseThrow(() -> new NotFoundException("Cart", customerId.toString()));
    }
}
