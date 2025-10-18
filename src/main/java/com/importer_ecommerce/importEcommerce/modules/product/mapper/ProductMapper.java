package com.importer_ecommerce.importEcommerce.modules.product.mapper;

import com.importer_ecommerce.importEcommerce.modules.product.dto.response.*;
import com.importer_ecommerce.importEcommerce.modules.product.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting Product entities to response DTOs
 * Handles all product-related entity to DTO conversions
 */
@Component
public class ProductMapper {
    
    /**
     * Convert Product entity to ProductResponse
     * Includes all related data (attributes, variants, specifications)
     */
    public ProductResponse toProductResponse(Product product) {
        if (product == null) {
            return null;
        }
        
        return new ProductResponse(
            product.getId(),
            product.getTitle(),
            product.getSlug(),
            product.getDescription(),
            product.getProfileImage(),
            product.getImages(),
            product.getDescriptionImages(),
            product.getMinimumOrderQuantity(),
            product.getStatus().name(),
            toCategoryInfo(product.getCategory()),
            toProductDetailVariantResponses(product.getVariants()),
            toProductSpecificationResponses(product.getSpecifications()),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
    
    /**
     * Convert Product entity to ProductSummaryResponse
     * Used for product lists where full details are not needed
     */
    public ProductSummaryResponse toProductSummaryResponse(Product product) {
        if (product == null) {
            return null;
        }
        
        // Calculate variant count and total stock
        int variantCount = product.getVariants() != null ? product.getVariants().size() : 0;
        int totalStock = product.getVariants() != null 
            ? product.getVariants().stream()
                .mapToInt(variant -> variant.getStockQuantity() != null ? variant.getStockQuantity() : 0)
                .sum()
            : 0;
        
        return new ProductSummaryResponse(
            product.getId(),
            product.getTitle(),
            product.getProfileImage(),
            product.getStatus().name(),
            toCategoryInfo(product.getCategory()),
            variantCount,
            totalStock,
            product.getCreatedAt()
        );
    }
    
    /**
     * Convert Category entity to CategoryInfo
     */
    private CategoryInfo toCategoryInfo(Category category) {
        if (category == null) {
            return null;
        }
        
        return new CategoryInfo(
            category.getId(),
            category.getTitle()
        );
    }
    
    /**
     * Convert list of ProductVariant entities to ProductDetailVariantResponse list
     */
    private List<ProductDetailVariantResponse> toProductDetailVariantResponses(List<ProductVariant> variants) {
        if (variants == null) {
            return List.of();
        }
        
        return variants.stream()
            .map(this::toProductDetailVariantResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Convert ProductVariant entity to ProductDetailVariantResponse
     */
    private ProductDetailVariantResponse toProductDetailVariantResponse(ProductVariant variant) {
        if (variant == null) {
            return null;
        }
        
        return new ProductDetailVariantResponse(
            variant.getId(),
            variant.getSku(),
            variant.getPrice(),
            variant.getCompareAtPrice(),
            variant.getStockQuantity(),
            variant.getIsActive(),
            toVariantAttributeDetailResponses(variant.getAttributeValues())
        );
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
    
    /**
     * Convert list of ProductSpecification entities to ProductSpecificationResponse list
     */
    private List<ProductSpecificationResponse> toProductSpecificationResponses(List<ProductSpecification> specifications) {
        if (specifications == null) {
            return List.of();
        }
        
        return specifications.stream()
            .map(this::toProductSpecificationResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Convert ProductSpecification entity to ProductSpecificationResponse
     */
    private ProductSpecificationResponse toProductSpecificationResponse(ProductSpecification specification) {
        if (specification == null) {
            return null;
        }
        
        return new ProductSpecificationResponse(
            specification.getId(),
            specification.getName(),
            specification.getValue(),
            specification.getCreatedAt(),
            specification.getUpdatedAt()
        );
    }
}
