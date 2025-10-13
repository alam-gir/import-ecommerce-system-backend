package com.importer_ecommerce.importEcommerce.modules.product.service;

import com.importer_ecommerce.importEcommerce.modules.product.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for ProductVariant management
 * Handles all product variant-related business operations
 */
public interface ProductVariantService {
    
    /**
     * Create a new product variant
     * Creates SKU with price, stock, and attribute combinations
     */
    boolean createProductVariant(UUID productId, String sku, BigDecimal price, 
                               BigDecimal compareAtPrice, BigDecimal costPrice,
                               Integer stockQuantity, Integer lowStockThreshold,
                               BigDecimal weight, String dimensions, String barcode,
                               Boolean isActive, Boolean isTracked, List<UUID> attributeValueIds);
    
    /**
     * Update an existing product variant
     * Modifies variant details and attribute combinations
     */
    boolean updateProductVariant(UUID variantId, String sku, BigDecimal price,
                               BigDecimal compareAtPrice, BigDecimal costPrice,
                               Integer stockQuantity, Integer lowStockThreshold,
                               BigDecimal weight, String dimensions, String barcode,
                               Boolean isActive, Boolean isTracked, List<UUID> attributeValueIds);
    
    /**
     * Get product variant by ID
     * Returns variant with all linked attribute values
     */
    ProductVariant getProductVariantById(UUID variantId);
    
    /**
     * Get all variants for a product
     * Returns all variants defined for a specific product
     */
    List<ProductVariant> getProductVariantsByProductId(UUID productId);
    
    /**
     * Get all variants for a product with pagination
     */
    Page<ProductVariant> getProductVariantsByProductId(UUID productId, Pageable pageable);
    
    /**
     * Delete product variant by ID
     * Removes variant and all its attribute links
     */
    boolean deleteProductVariant(UUID variantId);
    
    /**
     * Check if SKU exists (excluding current variant)
     * Used for validation during create/update operations
     */
    boolean existsBySku(String sku, UUID excludeId);
    
    /**
     * Update variant stock only
     * Quick stock update without modifying other details
     */
    boolean updateVariantStock(UUID variantId, Integer stockQuantity, Integer lowStockThreshold);
    
    /**
     * Link attribute values to a variant
     * Updates which attribute values are associated with a variant
     */
    boolean linkAttributeValuesToVariant(UUID variantId, List<UUID> attributeValueIds);
    
    /**
     * Get variants with low stock
     * Returns variants where stock is below threshold
     */
    List<ProductVariant> getVariantsWithLowStock();
    
    /**
     * Get variants out of stock
     * Returns variants with zero or negative stock
     */
    List<ProductVariant> getVariantsOutOfStock();
    
    /**
     * Get variants in stock
     * Returns variants with positive stock
     */
    List<ProductVariant> getVariantsInStock();
    
    /**
     * Get variants by price range
     * Returns variants within the specified price range
     */
    List<ProductVariant> getVariantsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Get variants with discount
     * Returns variants that have compare at price > current price
     */
    List<ProductVariant> getVariantsWithDiscount();
    
    /**
     * Get variants by attribute value
     * Returns variants that have a specific attribute value
     */
    List<ProductVariant> getVariantsByAttributeValue(UUID attributeValueId);
    
    /**
     * Get variants by multiple attribute values
     * Returns variants that have all the specified attribute values
     */
    List<ProductVariant> getVariantsByAttributeValues(List<UUID> attributeValueIds);
}

