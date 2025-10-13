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
            product.getDescription(),
            product.getProfileImage(),
            product.getImages(),
            product.getDescriptionImages(),
            product.getMinimumOrderQuantity(),
            toCategoryInfo(product.getCategory()),
            toVariantAttributeResponses(product.getVariantAttributes()),
            toProductVariantResponses(product.getVariants()),
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
            product.getDescription(),
            product.getProfileImage(),
            product.getMinimumOrderQuantity(),
            toCategoryInfo(product.getCategory()),
            variantCount,
            totalStock,
            product.getCreatedAt(),
            product.getUpdatedAt()
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
            category.getTitle(),
            category.getImage()
        );
    }
    
    /**
     * Convert list of VariantAttribute entities to VariantAttributeResponse list
     */
    private List<VariantAttributeResponse> toVariantAttributeResponses(List<VariantAttribute> attributes) {
        if (attributes == null) {
            return List.of();
        }
        
        return attributes.stream()
            .map(this::toVariantAttributeResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Convert VariantAttribute entity to VariantAttributeResponse
     */
    private VariantAttributeResponse toVariantAttributeResponse(VariantAttribute attribute) {
        if (attribute == null) {
            return null;
        }
        
        return new VariantAttributeResponse(
            attribute.getId(),
            attribute.getName(),
            attribute.getAttributeType().name(),
            toVariantAttributeValueResponses(attribute.getAttributeValues()),
            attribute.getCreatedAt(),
            attribute.getUpdatedAt()
        );
    }
    
    /**
     * Convert list of VariantAttributeValue entities to VariantAttributeValueResponse list
     */
    private List<VariantAttributeValueResponse> toVariantAttributeValueResponses(List<VariantAttributeValue> values) {
        if (values == null) {
            return List.of();
        }
        
        return values.stream()
            .map(this::toVariantAttributeValueResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Convert VariantAttributeValue entity to VariantAttributeValueResponse
     */
    private VariantAttributeValueResponse toVariantAttributeValueResponse(VariantAttributeValue value) {
        if (value == null) {
            return null;
        }
        
        return new VariantAttributeValueResponse(
            value.getId(),
            value.getValue(),
            value.getImageUrl(),
            value.getCreatedAt(),
            value.getUpdatedAt()
        );
    }
    
    /**
     * Convert list of ProductVariant entities to ProductVariantResponse list
     */
    private List<ProductVariantResponse> toProductVariantResponses(List<ProductVariant> variants) {
        if (variants == null) {
            return List.of();
        }
        
        return variants.stream()
            .map(this::toProductVariantResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Convert ProductVariant entity to ProductVariantResponse
     */
    private ProductVariantResponse toProductVariantResponse(ProductVariant variant) {
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
     * Convert list of VariantAttributeValue entities to AttributeValueInfo list
     * Used in variant context to show attribute names and values
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
