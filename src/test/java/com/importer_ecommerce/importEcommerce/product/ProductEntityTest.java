package com.importer_ecommerce.importEcommerce.product;

import com.importer_ecommerce.importEcommerce.product.entity.Product;
import static com.importer_ecommerce.importEcommerce.product.entity.Product.ProductStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductEntityTest {

    @Test
    void testProductCreationWithMinOrderQuantity() {
        Product product = new Product();
        product.setTitle("Test Product");
        product.setSlug("test-product");
        product.setMinOrderQuantity(5);
        product.setStatus(ProductStatus.DRAFT);
        
        assertEquals("Test Product", product.getTitle());
        assertEquals("test-product", product.getSlug());
        assertEquals(5, product.getMinOrderQuantity());
        assertEquals(ProductStatus.DRAFT, product.getStatus());
    }
    
    @Test
    void testProductDefaultMinOrderQuantity() {
        Product product = new Product();
        product.setTitle("Test Product");
        product.setSlug("test-product");
        product.setStatus(ProductStatus.DRAFT);
        
        // Should use default value of 1
        assertEquals(1, product.getMinOrderQuantity());
    }
    
    @Test
    void testProductUpdateMinOrderQuantity() {
        Product product = new Product();
        product.setTitle("Test Product");
        product.setSlug("test-product");
        product.setMinOrderQuantity(3);
        product.setStatus(ProductStatus.DRAFT);
        
        // Update min order quantity
        product.setMinOrderQuantity(10);
        assertEquals(10, product.getMinOrderQuantity());
    }
}
