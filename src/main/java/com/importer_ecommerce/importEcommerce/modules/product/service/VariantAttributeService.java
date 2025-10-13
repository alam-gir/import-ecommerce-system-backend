package com.importer_ecommerce.importEcommerce.modules.product.service;

import com.importer_ecommerce.importEcommerce.modules.product.entity.VariantAttribute;
import com.importer_ecommerce.importEcommerce.modules.product.entity.VariantAttributeValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for VariantAttribute management
 * Handles all variant attribute-related business operations
 */
public interface VariantAttributeService {
    
    /**
     * Create a new variant attribute for a product
     * Defines attributes like Color, Size, Material
     */
    boolean createVariantAttribute(UUID productId, String name, String attributeType);
    
    /**
     * Update an existing variant attribute
     * Modifies attribute name and type
     */
    boolean updateVariantAttribute(UUID attributeId, String name, String attributeType);
    
    /**
     * Get variant attribute by ID
     * Returns attribute with all its values
     */
    VariantAttribute getVariantAttributeById(UUID attributeId);
    
    /**
     * Get all variant attributes for a product
     * Returns all attributes defined for a specific product
     */
    List<VariantAttribute> getVariantAttributesByProductId(UUID productId);
    
    /**
     * Get all variant attributes for a product with pagination
     */
    Page<VariantAttribute> getVariantAttributesByProductId(UUID productId, Pageable pageable);
    
    /**
     * Delete variant attribute by ID
     * Checks for dependencies before deletion
     */
    boolean deleteVariantAttribute(UUID attributeId);
    
    /**
     * Check if attribute name exists for a product (excluding current attribute)
     * Used for validation during create/update operations
     */
    boolean existsByNameForProduct(String name, UUID productId, UUID excludeId);
    
    /**
     * Get attributes by type
     * Returns attributes of a specific type (TEXT, IMAGE, NUMBER)
     */
    List<VariantAttribute> getAttributesByType(String attributeType);
    
    /**
     * Get attributes used by variants
     * Returns attributes that are actually used by product variants
     */
    List<VariantAttribute> getAttributesUsedByVariants();
    
    /**
     * Get attributes without values
     * Returns attributes that don't have any values yet
     */
    List<VariantAttribute> getAttributesWithoutValues();
    
    /**
     * Create a new variant attribute value
     * Adds values like Red, Blue, Small, Large to an attribute
     */
    boolean createVariantAttributeValue(UUID attributeId, String value, MultipartFile image);
    
    /**
     * Update an existing variant attribute value
     * Modifies value text and image file
     */
    boolean updateVariantAttributeValue(UUID valueId, String value, MultipartFile image);
    
    /**
     * Get variant attribute value by ID
     */
    VariantAttributeValue getVariantAttributeValueById(UUID valueId);
    
    /**
     * Get all values for a variant attribute
     * Returns all values defined for a specific attribute
     */
    List<VariantAttributeValue> getVariantAttributeValuesByAttributeId(UUID attributeId);
    
    /**
     * Get all values for a variant attribute with pagination
     */
    Page<VariantAttributeValue> getVariantAttributeValuesByAttributeId(UUID attributeId, Pageable pageable);
    
    /**
     * Delete variant attribute value by ID
     * Checks for variant dependencies before deletion
     */
    boolean deleteVariantAttributeValue(UUID valueId);
    
    /**
     * Check if value exists for an attribute (excluding current value)
     * Used for validation during create/update operations
     */
    boolean existsByValueForAttribute(String value, UUID attributeId, UUID excludeId);
    
    /**
     * Get values with images
     * Returns values that have image URLs
     */
    List<VariantAttributeValue> getValuesWithImages();
    
    /**
     * Get values used by variants
     * Returns values that are actually used by product variants
     */
    List<VariantAttributeValue> getValuesUsedByVariants();
}
