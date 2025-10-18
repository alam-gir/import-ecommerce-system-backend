package com.importer_ecommerce.importEcommerce.modules.product.mapper;

import com.importer_ecommerce.importEcommerce.modules.product.dto.response.ProductVariantResponse;
import com.importer_ecommerce.importEcommerce.modules.product.dto.response.VariantAttributeDetailResponse;
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
     * Includes detailed attribute value information
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
            toVariantAttributeDetailResponses(variant.getAttributeValues()),
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
     * Convert list of VariantAttributeValue entities to VariantAttributeDetailResponse list
     * Returns detailed attribute and value information
     */
    private List<VariantAttributeDetailResponse> toVariantAttributeDetailResponses(List<VariantAttributeValue> values) {
        if (values == null) {
            return List.of();
        }
        
        return values.stream()
            .map(this::toVariantAttributeDetailResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Convert VariantAttributeValue entity to VariantAttributeDetailResponse
     * Includes complete attribute and value details
     */
    private VariantAttributeDetailResponse toVariantAttributeDetailResponse(VariantAttributeValue value) {
        if (value == null) {
            return null;
        }
        
        return new VariantAttributeDetailResponse(
            value.getVariantAttribute().getId(),
            value.getVariantAttribute().getName(),
            value.getVariantAttribute().getAttributeType().name(),
            value.getId(),
            value.getValue(),
            value.getImageUrl()
        );
    }
}

