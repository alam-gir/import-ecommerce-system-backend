package com.importer_ecommerce.importEcommerce.modules.product.service.impl;

import com.importer_ecommerce.importEcommerce.cloudflare.service.CloudflareService;
import com.importer_ecommerce.importEcommerce.common.exception.ApiException;
import com.importer_ecommerce.importEcommerce.common.exception.ConflictException;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.modules.product.entity.Product;
import com.importer_ecommerce.importEcommerce.modules.product.entity.VariantAttribute;
import com.importer_ecommerce.importEcommerce.modules.product.entity.VariantAttributeValue;
import com.importer_ecommerce.importEcommerce.modules.product.repository.ProductRepository;
import com.importer_ecommerce.importEcommerce.modules.product.repository.VariantAttributeRepository;
import com.importer_ecommerce.importEcommerce.modules.product.repository.VariantAttributeValueRepository;
import com.importer_ecommerce.importEcommerce.modules.product.service.VariantAttributeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of VariantAttributeService
 * Handles all variant attribute-related business operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VariantAttributeServiceImpl implements VariantAttributeService {
    
    private final VariantAttributeRepository variantAttributeRepository;
    private final VariantAttributeValueRepository variantAttributeValueRepository;
    private final ProductRepository productRepository;
    private final CloudflareService cloudflareService;
    
    @Override
    @Transactional
    public boolean createVariantAttribute(UUID productId, String name, String attributeType) {
        try {
            // Validate product exists
            Product product = productRepository.findByIdAndNotDeleted(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));
            
            // Check if attribute name already exists for this product
            if (existsByNameForProduct(name, productId, null)) {
                throw new ConflictException("Attribute with name '" + name + "' already exists for this product");
            }
            
            // Validate attribute type
            VariantAttribute.AttributeType type;
            try {
                type = VariantAttribute.AttributeType.valueOf(attributeType.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ConflictException("Invalid attribute type: " + attributeType);
            }
            
            // Create variant attribute entity
            VariantAttribute attribute = VariantAttribute.builder()
                .product(product)
                .name(name)
                .attributeType(type)
                .build();
            
            // Save attribute
            variantAttributeRepository.save(attribute);
            
            log.info("Variant attribute created successfully: {} for product: {}", name, productId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to create variant attribute: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create variant attribute: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean updateVariantAttribute(UUID attributeId, String name, String attributeType) {
        try {
            // Get existing attribute
            VariantAttribute attribute = getVariantAttributeById(attributeId);
            
            // Check if attribute name already exists for this product (excluding current attribute)
            if (existsByNameForProduct(name, attribute.getProduct().getId(), attributeId)) {
                throw new ConflictException("Attribute with name '" + name + "' already exists for this product");
            }
            
            // Validate attribute type
            VariantAttribute.AttributeType type;
            try {
                type = VariantAttribute.AttributeType.valueOf(attributeType.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ConflictException("Invalid attribute type: " + attributeType);
            }
            
            // Update attribute
            attribute.setName(name);
            attribute.setAttributeType(type);
            
            // Save updated attribute
            variantAttributeRepository.save(attribute);
            
            log.info("Variant attribute updated successfully: {} with ID: {}", name, attributeId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to update variant attribute: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update variant attribute: " + e.getMessage());
        }
    }
    
    @Override
    public VariantAttribute getVariantAttributeById(UUID attributeId) {
        return variantAttributeRepository.findByIdAndNotDeleted(attributeId)
            .orElseThrow(() -> new NotFoundException("Variant attribute not found with ID: " + attributeId));
    }
    
    @Override
    public List<VariantAttribute> getVariantAttributesByProductId(UUID productId) {
        return variantAttributeRepository.findByProductIdAndNotDeleted(productId);
    }
    
    @Override
    public Page<VariantAttribute> getVariantAttributesByProductId(UUID productId, Pageable pageable) {
        return variantAttributeRepository.findByProductIdAndNotDeleted(productId, pageable);
    }
    
    @Override
    @Transactional
    public boolean deleteVariantAttribute(UUID attributeId) {
        try {
            VariantAttribute attribute = getVariantAttributeById(attributeId);
            
            // Check if attribute has values
            List<VariantAttributeValue> values = variantAttributeValueRepository.findByVariantAttributeIdAndNotDeleted(attributeId);
            if (!values.isEmpty()) {
                throw new ConflictException("Cannot delete attribute with existing values. Delete values first.");
            }
            
            // Hard delete attribute
            variantAttributeRepository.delete(attribute);
            
            log.info("Variant attribute deleted successfully: {} with ID: {}", attribute.getName(), attributeId);
            return true;
            
        } catch (NotFoundException e) {
            // Re-throw NotFoundException to be handled by controller
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete variant attribute: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete variant attribute: " + e.getMessage());
        }
    }
    
    @Override
    public boolean existsByNameForProduct(String name, UUID productId, UUID excludeId) {
        return variantAttributeRepository.existsByProductIdAndNameAndNotDeleted(productId, name, excludeId);
    }
    
    @Override
    public List<VariantAttribute> getAttributesByType(String attributeType) {
        try {
            VariantAttribute.AttributeType type = VariantAttribute.AttributeType.valueOf(attributeType.toUpperCase());
            return variantAttributeRepository.findByAttributeType(type);
        } catch (IllegalArgumentException e) {
            throw new ConflictException("Invalid attribute type: " + attributeType);
        }
    }
    
    @Override
    public List<VariantAttribute> getAttributesUsedByVariants() {
        return variantAttributeRepository.findAttributesUsedByVariants();
    }
    
    @Override
    public List<VariantAttribute> getAttributesWithoutValues() {
        return variantAttributeRepository.findAttributesWithoutValues();
    }
    
    @Override
    @Transactional
    public boolean createVariantAttributeValue(UUID attributeId, String value, MultipartFile image) {
        try {
            // Validate attribute exists
            VariantAttribute attribute = getVariantAttributeById(attributeId);
            
            // Check if value already exists for this attribute
            if (existsByValueForAttribute(value, attributeId, null)) {
                throw new ConflictException("Value '" + value + "' already exists for this attribute");
            }
            
            // Upload image if provided
            String imageUrl = null;
            if (image != null && !image.isEmpty()) {
                imageUrl = cloudflareService.uploadFile(image, "attribute-values/" + attributeId + "/");
            }
            
            // Create variant attribute value entity
            VariantAttributeValue attributeValue = VariantAttributeValue.builder()
                .variantAttribute(attribute)
                .value(value)
                .imageUrl(imageUrl)
                .build();
            
            // Save attribute value
            variantAttributeValueRepository.save(attributeValue);
            
            log.info("Variant attribute value created successfully: {} for attribute: {}", value, attributeId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to create variant attribute value: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create variant attribute value: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean updateVariantAttributeValue(UUID valueId, String value, MultipartFile image) {
        try {
            // Get existing attribute value
            VariantAttributeValue attributeValue = getVariantAttributeValueById(valueId);
            
            // Check if value already exists for this attribute (excluding current value)
            if (existsByValueForAttribute(value, attributeValue.getVariantAttribute().getId(), valueId)) {
                throw new ConflictException("Value '" + value + "' already exists for this attribute");
            }
            
            // Handle image update
            if (image != null && !image.isEmpty()) {
                // Delete old image if exists
                if (attributeValue.getImageUrl() != null) {
                    cloudflareService.deleteFile(attributeValue.getImageUrl());
                }
                // Upload new image
                String imageUrl = cloudflareService.uploadFile(image, "attribute-values/" + attributeValue.getVariantAttribute().getId() + "/");
                attributeValue.setImageUrl(imageUrl);
            }
            
            // Update attribute value
            attributeValue.setValue(value);
            
            // Save updated attribute value
            variantAttributeValueRepository.save(attributeValue);
            
            log.info("Variant attribute value updated successfully: {} with ID: {}", value, valueId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to update variant attribute value: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update variant attribute value: " + e.getMessage());
        }
    }
    
    @Override
    public VariantAttributeValue getVariantAttributeValueById(UUID valueId) {
        return variantAttributeValueRepository.findByIdAndNotDeleted(valueId)
            .orElseThrow(() -> new NotFoundException("Variant attribute value not found with ID: " + valueId));
    }
    
    @Override
    public List<VariantAttributeValue> getVariantAttributeValuesByAttributeId(UUID attributeId) {
        return variantAttributeValueRepository.findByVariantAttributeIdAndNotDeleted(attributeId);
    }
    
    @Override
    public Page<VariantAttributeValue> getVariantAttributeValuesByAttributeId(UUID attributeId, Pageable pageable) {
        return variantAttributeValueRepository.findByVariantAttributeIdAndNotDeleted(attributeId, pageable);
    }
    
    @Override
    @Transactional
    public boolean deleteVariantAttributeValue(UUID valueId) {
        try {
            VariantAttributeValue attributeValue = getVariantAttributeValueById(valueId);
            
            // Check if value is used by variants
            // This would require checking if any variants are linked to this value
            // For now, we'll allow deletion - can be enhanced later
            
            // Delete image if exists
            if (attributeValue.getImageUrl() != null) {
                cloudflareService.deleteFile(attributeValue.getImageUrl());
            }
            
            // Hard delete attribute value
            variantAttributeValueRepository.delete(attributeValue);
            
            log.info("Variant attribute value deleted successfully: {} with ID: {}", attributeValue.getValue(), valueId);
            return true;
            
        } catch (NotFoundException e) {
            // Re-throw NotFoundException to be handled by controller
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete variant attribute value: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete variant attribute value: " + e.getMessage());
        }
    }
    
    @Override
    public boolean existsByValueForAttribute(String value, UUID attributeId, UUID excludeId) {
        return variantAttributeValueRepository.existsByVariantAttributeIdAndValueAndNotDeleted(attributeId, value, excludeId);
    }
    
    @Override
    public List<VariantAttributeValue> getValuesWithImages() {
        return variantAttributeValueRepository.findValuesWithImages();
    }
    
    @Override
    public List<VariantAttributeValue> getValuesUsedByVariants() {
        return variantAttributeValueRepository.findValuesUsedByVariants();
    }
}
