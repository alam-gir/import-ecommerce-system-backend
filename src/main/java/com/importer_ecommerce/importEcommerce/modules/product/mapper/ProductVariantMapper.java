package com.importer_ecommerce.importEcommerce.modules.product.mapper;

import com.importer_ecommerce.importEcommerce.modules.product.dto.response.AttributeValueInfo;
import com.importer_ecommerce.importEcommerce.modules.product.dto.response.ProductVariantResponse;
import com.importer_ecommerce.importEcommerce.modules.product.entity.ProductVariant;
import com.importer_ecommerce.importEcommerce.modules.product.entity.VariantAttributeValue;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting ProductVariant entities to response DTOs
 * Handles variant and attribute value link conversions
 */
@Component
public class ProductVariantMapper {
    
    /**
     * Convert ProductVariant entity to ProductVariantResponse
     * Includes all linked attribute values
     */
    public ProductVariantResponse toProductVariantResponse(ProductVariant variant) {
        if (variant == null) {
            return null;
        }
        
        return new ProductVariantResponse(
            variant.getId(),
            variant.getSku(),
            variant.getPrice(),
            variant.getCompareAtPrice(),
            variant.getCostPrice(),
            variant.getStockQuantity(),
            variant.getLowStockThreshold(),
            variant.getWeight(),
            variant.getDimensions(),
            variant.getBarcode(),
            variant.getIsActive(),
            variant.getIsTracked(),
            toAttributeValueInfos(variant.getAttributeValues()),
            variant.getCreatedAt(),
            variant.getUpdatedAt()
        );
    }
    
    /**
     * Convert list of ProductVariant entities to ProductVariantResponse list
     */
    public List<ProductVariantResponse> toProductVariantResponses(List<ProductVariant> variants) {
        if (variants == null) {
            return List.of();
        }
        
        return variants.stream()
            .map(this::toProductVariantResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Convert list of VariantAttributeValue entities to AttributeValueInfo list
     * Shows attribute names and values linked to a variant
     */
    private List<AttributeValueInfo> toAttributeValueInfos(List<VariantAttributeValue> values) {
        if (values == null) {
            return List.of();
        }
        
        return values.stream()
            .map(this::toAttributeValueInfo)
            .collect(Collectors.toList());
    }
    
    /**
     * Convert VariantAttributeValue entity to AttributeValueInfo
     * Shows attribute name, value, and image URL
     */
    private AttributeValueInfo toAttributeValueInfo(VariantAttributeValue value) {
        if (value == null) {
            return null;
        }
        
        String attributeName = value.getVariantAttribute() != null 
            ? value.getVariantAttribute().getName() 
            : null;
        
        return new AttributeValueInfo(
            value.getId(),
            attributeName,
            value.getValue(),
            value.getImageUrl()
        );
    }
}

