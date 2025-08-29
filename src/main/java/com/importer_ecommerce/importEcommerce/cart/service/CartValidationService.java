package com.importer_ecommerce.importEcommerce.cart.service;

import com.importer_ecommerce.importEcommerce.cart.dto.request.AddToCartRequest;
import com.importer_ecommerce.importEcommerce.cart.dto.validation.CartValidationResult;
import com.importer_ecommerce.importEcommerce.cart.dto.validation.ValidationError;
import com.importer_ecommerce.importEcommerce.cart.entity.Cart;
import com.importer_ecommerce.importEcommerce.cart.entity.CartItem;
import com.importer_ecommerce.importEcommerce.product.entity.Product;
import com.importer_ecommerce.importEcommerce.product.entity.ProductVariant;
import com.importer_ecommerce.importEcommerce.product.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartValidationService {
    
    private final ProductVariantRepository productVariantRepository;
    
    public CartValidationResult validateAddToCart(AddToCartRequest request, UUID customerId) {
        List<ValidationError> errors = new ArrayList<>();
        
        try {
            // Get product variant
            ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new RuntimeException("Product variant not found"));
            Product product = variant.getProduct();
            
            // Check if variant is active
            if (!variant.getIsActive()) {
                errors.add(new ValidationError(
                    "variantId",
                    "❌ This product variant is currently unavailable",
                    "Please select a different variant or check back later",
                    "VARIANT_INACTIVE"
                ));
                return new CartValidationResult(false, errors);
            }
            
            // Check if product is published
            if (product.getStatus() != Product.ProductStatus.PUBLISHED) {
                errors.add(new ValidationError(
                    "variantId",
                    "❌ This product is currently unavailable",
                    "Please check back later or contact support",
                    "PRODUCT_NOT_PUBLISHED"
                ));
                return new CartValidationResult(false, errors);
            }
            
            // Check minimum order quantity with friendly message
            if (request.getQuantity() < product.getMinOrderQuantity()) {
                errors.add(new ValidationError(
                    "quantity",
                    String.format("⚠️ Minimum order quantity for '%s' is %d", 
                        product.getTitle(), 
                        product.getMinOrderQuantity()),
                    String.format("Please increase quantity to %d or more", 
                        product.getMinOrderQuantity()),
                    "MIN_ORDER_QUANTITY_VIOLATION"
                ));
            }
            
            // Check inventory with helpful suggestions
            if (variant.getInventoryQuantity() < request.getQuantity()) {
                int available = variant.getInventoryQuantity();
                errors.add(new ValidationError(
                    "quantity",
                    String.format("📦 Only %d units available for '%s'", 
                        available, 
                        product.getTitle()),
                    String.format("Please reduce quantity to %d or less", 
                        available),
                    "INSUFFICIENT_STOCK"
                ));
            }
            
            // Check if quantity is zero
            if (variant.getInventoryQuantity() == 0) {
                errors.add(new ValidationError(
                    "quantity",
                    String.format("📦 '%s' is currently out of stock", 
                        product.getTitle()),
                    "Please check back later or contact us for availability",
                    "OUT_OF_STOCK"
                ));
            }
            
        } catch (Exception e) {
            log.error("Error validating add to cart request: {}", e.getMessage(), e);
            errors.add(new ValidationError(
                "general",
                "❌ Unable to validate item. Please try again",
                "Refresh the page and try again",
                "VALIDATION_ERROR"
            ));
        }
        
        return new CartValidationResult(errors.isEmpty(), errors);
    }
    
    public CartValidationResult validateCartForCheckout(Cart cart) {
        List<ValidationError> errors = new ArrayList<>();
        
        if (cart.isEmpty()) {
            errors.add(new ValidationError(
                "cart",
                "🛒 Your cart is empty",
                "Add some products to your cart before checkout",
                "EMPTY_CART"
            ));
            return new CartValidationResult(false, errors);
        }
        
        for (CartItem item : cart.getItems()) {
            ProductVariant variant = item.getProductVariant();
            Product product = variant.getProduct();
            
            // Re-validate minimum order quantity
            if (item.getQuantity() < product.getMinOrderQuantity()) {
                errors.add(new ValidationError(
                    "cart_item_" + item.getId(),
                    String.format("⚠️ '%s' quantity (%d) is below minimum (%d)", 
                        product.getTitle(),
                        item.getQuantity(),
                        product.getMinOrderQuantity()),
                    String.format("Increase quantity to %d or remove item", 
                        product.getMinOrderQuantity()),
                    "MIN_ORDER_QUANTITY_VIOLATION"
                ));
            }
            
            // Re-validate inventory
            if (variant.getInventoryQuantity() < item.getQuantity()) {
                errors.add(new ValidationError(
                    "cart_item_" + item.getId(),
                    String.format("📦 '%s' quantity (%d) exceeds available stock (%d)", 
                        product.getTitle(),
                        item.getQuantity(),
                        variant.getInventoryQuantity()),
                    String.format("Reduce quantity to %d or remove item", 
                        variant.getInventoryQuantity()),
                    "INSUFFICIENT_STOCK"
                ));
            }
            
            // Check if variant is still active
            if (!variant.getIsActive()) {
                errors.add(new ValidationError(
                    "cart_item_" + item.getId(),
                    String.format("❌ '%s' is no longer available", 
                        product.getTitle()),
                    "Remove this item from your cart",
                    "VARIANT_INACTIVE"
                ));
            }
            
            // Check if product is still published
            if (product.getStatus() != Product.ProductStatus.PUBLISHED) {
                errors.add(new ValidationError(
                    "cart_item_" + item.getId(),
                    String.format("❌ '%s' is no longer available", 
                        product.getTitle()),
                    "Remove this item from your cart",
                    "PRODUCT_NOT_PUBLISHED"
                ));
            }
        }
        
        return new CartValidationResult(errors.isEmpty(), errors);
    }
}
