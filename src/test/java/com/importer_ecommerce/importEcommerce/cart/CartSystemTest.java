package com.importer_ecommerce.importEcommerce.cart;

import com.importer_ecommerce.importEcommerce.cart.dto.request.AddToCartRequest;
import com.importer_ecommerce.importEcommerce.cart.dto.response.CartSummaryResponse;
import com.importer_ecommerce.importEcommerce.cart.entity.Cart;
import com.importer_ecommerce.importEcommerce.cart.service.CartCalculationService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;

class CartSystemTest {

    @Test
    void testCartSystemComponents() {
        // Test that we can create cart-related objects
        assertNotNull(new Cart());
        assertNotNull(new AddToCartRequest());
        assertNotNull(new CartSummaryResponse(
            BigDecimal.ZERO, 0, 0, 
            java.util.List.of(), false, "Test message"
        ));
    }

    @Test
    void testCartCalculationService() {
        CartCalculationService service = new CartCalculationService();
        Cart emptyCart = new Cart();
        
        CartSummaryResponse summary = service.calculateCartSummary(emptyCart);
        
        assertNotNull(summary);
        assertEquals(BigDecimal.ZERO, summary.subtotal());
        assertEquals(0, summary.totalItems());
        assertFalse(summary.canProceedToCheckout());
        assertTrue(summary.nextStepMessage().contains("empty"));
    }
}
