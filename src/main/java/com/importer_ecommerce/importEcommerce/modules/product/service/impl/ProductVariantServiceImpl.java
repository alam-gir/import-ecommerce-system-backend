package com.importer_ecommerce.importEcommerce.modules.product.service.impl;

import com.importer_ecommerce.importEcommerce.common.exception.ApiException;
import com.importer_ecommerce.importEcommerce.common.exception.ConflictException;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.modules.product.entity.Product;
import com.importer_ecommerce.importEcommerce.modules.product.entity.ProductVariant;
import com.importer_ecommerce.importEcommerce.modules.product.entity.VariantAttributeValue;
import com.importer_ecommerce.importEcommerce.modules.product.repository.ProductRepository;
import com.importer_ecommerce.importEcommerce.modules.product.repository.ProductVariantRepository;
import com.importer_ecommerce.importEcommerce.modules.product.repository.VariantAttributeValueRepository;
import com.importer_ecommerce.importEcommerce.modules.product.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of ProductVariantService
 * Handles all product variant-related business operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductVariantServiceImpl implements ProductVariantService {
    
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final VariantAttributeValueRepository variantAttributeValueRepository;
    
    @Override
    @Transactional
    public boolean createProductVariant(UUID productId, String sku, BigDecimal price,
                                       BigDecimal compareAtPrice, BigDecimal costPrice,
                                       Integer stockQuantity, Integer lowStockThreshold,
                                       BigDecimal weight, String dimensions, String barcode,
                                       Boolean isActive, Boolean isTracked, List<UUID> attributeValueIds) {
        try {
            // Validate product exists
            Product product = productRepository.findByIdAndNotDeleted(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));
            
            // Check if SKU already exists
            if (existsBySku(sku, null)) {
                throw new ConflictException("Variant with SKU '" + sku + "' already exists");
            }
            
            // Create product variant entity
            ProductVariant variant = ProductVariant.builder()
                .product(product)
                .sku(sku)
                .price(price)
                .compareAtPrice(compareAtPrice)
                .costPrice(costPrice)
                .stockQuantity(stockQuantity)
                .lowStockThreshold(lowStockThreshold)
                .weight(weight)
                .dimensions(dimensions)
                .barcode(barcode)
                .isActive(isActive != null ? isActive : true)
                .isTracked(isTracked != null ? isTracked : true)
                .build();
            
            // Save variant first to get ID
            productVariantRepository.save(variant);
            
            // Link attribute values if provided
            if (attributeValueIds != null && !attributeValueIds.isEmpty()) {
                linkAttributeValuesToVariant(variant.getId(), attributeValueIds);
            }
            
            log.info("Product variant created successfully: {} for product: {}", sku, productId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to create product variant: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create product variant: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean updateProductVariant(UUID variantId, String sku, BigDecimal price,
                                       BigDecimal compareAtPrice, BigDecimal costPrice,
                                       Integer stockQuantity, Integer lowStockThreshold,
                                       BigDecimal weight, String dimensions, String barcode,
                                       Boolean isActive, Boolean isTracked, List<UUID> attributeValueIds) {
        try {
            // Get existing variant
            ProductVariant variant = getProductVariantById(variantId);
            
            // Check if SKU already exists (excluding current variant)
            if (existsBySku(sku, variantId)) {
                throw new ConflictException("Variant with SKU '" + sku + "' already exists");
            }
            
            // Update variant fields
            variant.setSku(sku);
            variant.setPrice(price);
            variant.setCompareAtPrice(compareAtPrice);
            variant.setCostPrice(costPrice);
            variant.setStockQuantity(stockQuantity);
            variant.setLowStockThreshold(lowStockThreshold);
            variant.setWeight(weight);
            variant.setDimensions(dimensions);
            variant.setBarcode(barcode);
            
            if (isActive != null) {
                variant.setIsActive(isActive);
            }
            if (isTracked != null) {
                variant.setIsTracked(isTracked);
            }
            
            // Save updated variant
            productVariantRepository.save(variant);
            
            // Update attribute value links if provided
            if (attributeValueIds != null) {
                linkAttributeValuesToVariant(variantId, attributeValueIds);
            }
            
            log.info("Product variant updated successfully: {} with ID: {}", sku, variantId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to update product variant: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update product variant: " + e.getMessage());
        }
    }
    
    @Override
    public ProductVariant getProductVariantById(UUID variantId) {
        return productVariantRepository.findByIdAndNotDeleted(variantId)
            .orElseThrow(() -> new NotFoundException("Product variant not found with ID: " + variantId));
    }
    
    @Override
    public List<ProductVariant> getProductVariantsByProductId(UUID productId) {
        return productVariantRepository.findByProductIdAndNotDeleted(productId);
    }
    
    @Override
    public Page<ProductVariant> getProductVariantsByProductId(UUID productId, Pageable pageable) {
        return productVariantRepository.findByProductIdAndNotDeleted(productId, pageable);
    }
    
    @Override
    @Transactional
    public boolean deleteProductVariant(UUID variantId) {
        try {
            ProductVariant variant = getProductVariantById(variantId);
            
            // Clear attribute value links
            variant.getAttributeValues().clear();
            productVariantRepository.save(variant);
            
            // Hard delete variant
            productVariantRepository.delete(variant);
            
            log.info("Product variant deleted successfully: {} with ID: {}", variant.getSku(), variantId);
            return true;
            
        } catch (NotFoundException e) {
            // Re-throw NotFoundException to be handled by controller
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete product variant: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete product variant: " + e.getMessage());
        }
    }
    
    @Override
    public boolean existsBySku(String sku, UUID excludeId) {
        return productVariantRepository.existsBySkuAndNotDeleted(sku, excludeId);
    }
    
    @Override
    @Transactional
    public boolean updateVariantStock(UUID variantId, Integer stockQuantity, Integer lowStockThreshold) {
        try {
            ProductVariant variant = getProductVariantById(variantId);
            
            variant.setStockQuantity(stockQuantity);
            if (lowStockThreshold != null) {
                variant.setLowStockThreshold(lowStockThreshold);
            }
            
            productVariantRepository.save(variant);
            
            log.info("Variant stock updated successfully: {} with ID: {}", variant.getSku(), variantId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to update variant stock: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update variant stock: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean linkAttributeValuesToVariant(UUID variantId, List<UUID> attributeValueIds) {
        try {
            ProductVariant variant = getProductVariantById(variantId);
            
            // Clear existing attribute value links
            variant.getAttributeValues().clear();
            
            // Add new attribute value links
            if (attributeValueIds != null && !attributeValueIds.isEmpty()) {
                for (UUID valueId : attributeValueIds) {
                    VariantAttributeValue attributeValue = variantAttributeValueRepository.findByIdAndNotDeleted(valueId)
                        .orElseThrow(() -> new NotFoundException("Attribute value not found with ID: " + valueId));
                    
                    variant.addAttributeValue(attributeValue);
                }
            }
            
            productVariantRepository.save(variant);
            
            log.info("Attribute values linked to variant successfully: {} with ID: {}", variant.getSku(), variantId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to link attribute values to variant: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to link attribute values to variant: " + e.getMessage());
        }
    }
    
    @Override
    public List<ProductVariant> getVariantsWithLowStock() {
        return productVariantRepository.findVariantsWithLowStock();
    }
    
    @Override
    public List<ProductVariant> getVariantsOutOfStock() {
        return productVariantRepository.findVariantsOutOfStock();
    }
    
    @Override
    public List<ProductVariant> getVariantsInStock() {
        return productVariantRepository.findVariantsInStock();
    }
    
    @Override
    public List<ProductVariant> getVariantsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productVariantRepository.findByPriceRange(minPrice, maxPrice);
    }
    
    @Override
    public List<ProductVariant> getVariantsWithDiscount() {
        return productVariantRepository.findVariantsWithDiscount();
    }
    
    @Override
    public List<ProductVariant> getVariantsByAttributeValue(UUID attributeValueId) {
        return productVariantRepository.findByAttributeValueId(attributeValueId);
    }
    
    @Override
    public List<ProductVariant> getVariantsByAttributeValues(List<UUID> attributeValueIds) {
        return productVariantRepository.findByAttributeValueIds(attributeValueIds, (long) attributeValueIds.size());
    }
}

