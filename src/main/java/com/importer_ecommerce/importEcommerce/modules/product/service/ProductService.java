package com.importer_ecommerce.importEcommerce.modules.product.service;

import com.importer_ecommerce.importEcommerce.modules.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Product management
 * Handles all product-related business operations
 */
public interface ProductService {
    
    /**
     * Create a new product
     * Handles file uploads and basic product information
     */
    boolean createProduct(String title, String description, String categoryId, 
                         Integer minimumOrderQuantity, MultipartFile profileImage,
                         List<MultipartFile> images, List<MultipartFile> descriptionImages);
    
    /**
     * Update an existing product
     * Handles file uploads and updates existing files
     */
    boolean updateProduct(UUID productId, String title, String description, String categoryId,
                        Integer minimumOrderQuantity, MultipartFile profileImage,
                        List<MultipartFile> images, List<MultipartFile> descriptionImages,
                        List<String> existingImages, List<String> existingDescriptionImages);
    
    /**
     * Get product by ID with all related data
     * Returns product with attributes, variants, and specifications
     */
    Product getProductById(UUID productId);
    
    /**
     * Get product by ID without related data
     * Returns only basic product information
     */
    Product getProductBasicById(UUID productId);
    
    /**
     * Get all products with pagination and filtering
     * Supports search by title and filter by category
     */
    Page<Product> getAllProducts(Pageable pageable, String search, String categoryId);
    
    /**
     * Get products by category
     * Returns all products in a specific category
     */
    List<Product> getProductsByCategory(UUID categoryId);
    
    /**
     * Delete product by ID
     * Soft deletes the product and handles file cleanup
     */
    boolean deleteProduct(UUID productId);
    
    /**
     * Check if product title exists (excluding current product)
     * Used for validation during create/update operations
     */
    boolean existsByTitle(String title, UUID excludeId);
    
    /**
     * Get products with low stock
     * Returns products that have variants with low stock
     */
    List<Product> getProductsWithLowStock();
    
    /**
     * Get products with no variants
     * Returns products that don't have any variants yet
     */
    List<Product> getProductsWithoutVariants();
    
    /**
     * Get products with variants
     * Returns products that have at least one variant
     */
    List<Product> getProductsWithVariants();
    
    /**
     * Get products by minimum order quantity range
     * Returns products within the specified quantity range
     */
    List<Product> getProductsByMinimumOrderQuantityRange(Integer minQuantity, Integer maxQuantity);
}

