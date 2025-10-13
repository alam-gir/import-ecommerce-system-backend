package com.importer_ecommerce.importEcommerce.modules.product.service;

import com.importer_ecommerce.importEcommerce.modules.product.entity.ProductSpecification;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for ProductSpecification management
 * Handles all product specification-related business operations
 */
public interface ProductSpecificationService {
    
    /**
     * Create a new product specification
     * Adds specifications like Material, Care instructions, etc.
     */
    boolean createProductSpecification(UUID productId, String name, String value);
    
    /**
     * Update an existing product specification
     * Modifies specification name and value
     */
    boolean updateProductSpecification(UUID specificationId, String name, String value);
    
    /**
     * Get product specification by ID
     */
    ProductSpecification getProductSpecificationById(UUID specificationId);
    
    /**
     * Get all specifications for a product
     * Returns all specifications defined for a specific product
     */
    List<ProductSpecification> getProductSpecificationsByProductId(UUID productId);
    
    /**
     * Delete product specification by ID
     * Removes specification from the product
     */
    boolean deleteProductSpecification(UUID specificationId);
    
    /**
     * Check if specification name exists for a product (excluding current specification)
     * Used for validation during create/update operations
     */
    boolean existsByNameForProduct(String name, UUID productId, UUID excludeId);
}

