package com.importer_ecommerce.importEcommerce.modules.product.service.impl;

import com.importer_ecommerce.importEcommerce.common.exception.ApiException;
import com.importer_ecommerce.importEcommerce.common.exception.ConflictException;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.modules.product.entity.Product;
import com.importer_ecommerce.importEcommerce.modules.product.entity.ProductSpecification;
import com.importer_ecommerce.importEcommerce.modules.product.repository.ProductRepository;
import com.importer_ecommerce.importEcommerce.modules.product.repository.ProductSpecificationRepository;
import com.importer_ecommerce.importEcommerce.modules.product.service.ProductSpecificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of ProductSpecificationService
 * Handles all product specification-related business operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSpecificationServiceImpl implements ProductSpecificationService {
    
    private final ProductSpecificationRepository productSpecificationRepository;
    private final ProductRepository productRepository;
    
    @Override
    @Transactional
    public boolean createProductSpecification(UUID productId, String name, String value) {
        try {
            // Validate product exists
            Product product = productRepository.findByIdAndNotDeleted(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));
            
            // Check if specification name already exists for this product
            if (existsByNameForProduct(name, productId, null)) {
                throw new ConflictException("Specification with name '" + name + "' already exists for this product");
            }
            
            // Create product specification entity
            ProductSpecification specification = ProductSpecification.builder()
                .product(product)
                .name(name)
                .value(value)
                .build();
            
            // Save specification
            productSpecificationRepository.save(specification);
            
            log.info("Product specification created successfully: {} for product: {}", name, productId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to create product specification: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create product specification: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean updateProductSpecification(UUID specificationId, String name, String value) {
        try {
            // Get existing specification
            ProductSpecification specification = getProductSpecificationById(specificationId);
            
            // Check if specification name already exists for this product (excluding current specification)
            if (existsByNameForProduct(name, specification.getProduct().getId(), specificationId)) {
                throw new ConflictException("Specification with name '" + name + "' already exists for this product");
            }
            
            // Update specification
            specification.setName(name);
            specification.setValue(value);
            
            // Save updated specification
            productSpecificationRepository.save(specification);
            
            log.info("Product specification updated successfully: {} with ID: {}", name, specificationId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to update product specification: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update product specification: " + e.getMessage());
        }
    }
    
    @Override
    public ProductSpecification getProductSpecificationById(UUID specificationId) {
        return productSpecificationRepository.findByIdAndNotDeleted(specificationId)
            .orElseThrow(() -> new NotFoundException("Product specification not found with ID: " + specificationId));
    }
    
    @Override
    public List<ProductSpecification> getProductSpecificationsByProductId(UUID productId) {
        return productSpecificationRepository.findByProductIdAndNotDeleted(productId);
    }
    
    @Override
    @Transactional
    public boolean deleteProductSpecification(UUID specificationId) {
        try {
            ProductSpecification specification = getProductSpecificationById(specificationId);
            
            // Hard delete specification
            productSpecificationRepository.delete(specification);
            
            log.info("Product specification deleted successfully: {} with ID: {}", specification.getName(), specificationId);
            return true;
            
        } catch (NotFoundException e) {
            // Re-throw NotFoundException to be handled by controller
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete product specification: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete product specification: " + e.getMessage());
        }
    }
    
    @Override
    public boolean existsByNameForProduct(String name, UUID productId, UUID excludeId) {
        return productSpecificationRepository.existsByProductIdAndNameAndNotDeleted(productId, name, excludeId);
    }
}

