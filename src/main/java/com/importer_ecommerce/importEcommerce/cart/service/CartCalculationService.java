package com.importer_ecommerce.importEcommerce.cart.service;

import com.importer_ecommerce.importEcommerce.cart.dto.response.CartSummaryResponse;
import com.importer_ecommerce.importEcommerce.cart.entity.Cart;
import com.importer_ecommerce.importEcommerce.cart.entity.CartItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartCalculationService {
    
    public CartSummaryResponse calculateCartSummary(Cart cart) {
        if (cart.isEmpty()) {
            return new CartSummaryResponse(
                BigDecimal.ZERO,
                0,
                0,
                new ArrayList<>(),
                false,
                "🛒 Your cart is empty. Add some products to get started!"
            );
        }
        
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalItems = 0;
        List<String> warnings = new ArrayList<>();
        
        for (CartItem item : cart.getItems()) {
            try {
                BigDecimal itemTotal = item.getTotalPrice();
                subtotal = subtotal.add(itemTotal);
                totalItems += item.getQuantity();
                
                // Smart warnings for customer
                if (item.getQuantity() < item.getMinOrderQuantity()) {
                    warnings.add(String.format(
                        "⚠️ '%s' quantity (%d) is below minimum (%d). " +
                        "Please increase to %d or remove item.",
                        item.getProductTitle(),
                        item.getQuantity(),
                        item.getMinOrderQuantity(),
                        item.getMinOrderQuantity()
                    ));
                }
                
                if (!item.hasStockAvailable(item.getProductVariant().getInventoryQuantity())) {
                    warnings.add(String.format(
                        "📦 '%s' quantity (%d) exceeds available stock (%d). " +
                        "Please reduce to %d or remove item.",
                        item.getProductTitle(),
                        item.getQuantity(),
                        item.getProductVariant().getInventoryQuantity(),
                        item.getProductVariant().getInventoryQuantity()
                    ));
                }
                
                // Check if variant is still active
                if (!item.getProductVariant().getIsActive()) {
                    warnings.add(String.format(
                        "❌ '%s' is no longer available. " +
                        "Please remove this item from your cart.",
                        item.getProductTitle()
                    ));
                }
                
            } catch (Exception e) {
                log.error("Error calculating item total for cart item: {}", item.getId(), e);
                warnings.add(String.format(
                    "❌ Error calculating price for '%s'. Please refresh and try again.",
                    item.getProductTitle()
                ));
            }
        }
        
        // Round subtotal to 2 decimal places
        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        
        boolean canProceedToCheckout = warnings.isEmpty();
        String nextStepMessage = generateNextStepMessage(warnings, totalItems);
        
        return new CartSummaryResponse(
            subtotal,
            totalItems,
            cart.getItems().size(),
            warnings,
            canProceedToCheckout,
            nextStepMessage
        );
    }
    
    private String generateNextStepMessage(List<String> warnings, int totalItems) {
        if (warnings.isEmpty()) {
            if (totalItems == 1) {
                return "✅ Ready to checkout! You have 1 item in your cart.";
            } else {
                return String.format("✅ Ready to checkout! You have %d items in your cart.", totalItems);
            }
        } else {
            if (warnings.size() == 1) {
                return "⚠️ 1 issue found. Please resolve it before checkout.";
            } else {
                return String.format("⚠️ %d issues found. Please resolve them before checkout.", warnings.size());
            }
        }
    }
    
    public BigDecimal calculateItemTotal(CartItem item) {
        try {
            return item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        } catch (Exception e) {
            log.error("Error calculating item total: {}", e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }
    
    public BigDecimal calculateCartSubtotal(Cart cart) {
        if (cart.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return cart.getItems().stream()
            .map(this::calculateItemTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);
    }
}
