package com.importer_ecommerce.importEcommerce.cart.service;

import com.importer_ecommerce.importEcommerce.cart.dto.response.CartItemResponse;
import com.importer_ecommerce.importEcommerce.cart.dto.response.CartResponse;
import com.importer_ecommerce.importEcommerce.cart.dto.response.CartSummaryResponse;
import com.importer_ecommerce.importEcommerce.cart.entity.Cart;
import com.importer_ecommerce.importEcommerce.cart.entity.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CartMapperService {
    
    private final CartCalculationService cartCalculationService;
    
    public CartResponse toCartResponse(Cart cart, String lastAddedItemName) {
        CartSummaryResponse summary = cartCalculationService.calculateCartSummary(cart);
        
        List<CartItemResponse> itemResponses = cart.getItems().stream()
            .map(this::toCartItemResponse)
            .collect(Collectors.toList());
        
        return new CartResponse(
            cart.getId(),
            itemResponses,
            summary,
            lastAddedItemName,
            cart.getUpdatedAt(),
            !summary.warnings().isEmpty(),
            summary.warnings(),
            summary.canProceedToCheckout()
        );
    }
    
    public CartItemResponse toCartItemResponse(CartItem item) {
        BigDecimal totalPrice = item.getTotalPrice();
        BigDecimal compareAtPrice = item.getProductVariant().getCompareAtPrice();
        
        boolean hasDiscount = compareAtPrice != null && compareAtPrice.compareTo(item.getUnitPrice()) > 0;
        BigDecimal discountAmount = BigDecimal.ZERO;
        String discountPercentage = null;
        
        if (hasDiscount) {
            discountAmount = compareAtPrice.subtract(item.getUnitPrice());
            BigDecimal percentage = discountAmount
                .divide(compareAtPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            discountPercentage = percentage.setScale(1, RoundingMode.HALF_UP) + "%";
        }
        
        return new CartItemResponse(
            item.getId(),
            item.getProductVariant().getId(),
            item.getProductVariant().getProduct().getId(),
            item.getProductTitle(),
            item.getVariantTitle(),
            item.getMainProductImage(),
            item.getUnitPrice(),
            compareAtPrice,
            item.getQuantity(),
            item.getMinOrderQuantity(),
            item.getProductVariant().getInventoryQuantity(),
            totalPrice,
            hasDiscount,
            discountAmount,
            discountPercentage,
            item.getCreatedAt(),
            item.getUpdatedAt()
        );
    }
    
    public CartSummaryResponse toCartSummaryResponse(Cart cart) {
        return cartCalculationService.calculateCartSummary(cart);
    }
}
