package com.importer_ecommerce.importEcommerce.modules.product.mapper;

import com.importer_ecommerce.importEcommerce.modules.product.dto.response.ProductSpecificationResponse;
import com.importer_ecommerce.importEcommerce.modules.product.entity.ProductSpecification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting ProductSpecification entities to response DTOs
 * Handles specification conversions
 */
@Component
public class ProductSpecificationMapper {
    
    /**
     * Convert ProductSpecification entity to ProductSpecificationResponse
     */
    public ProductSpecificationResponse toProductSpecificationResponse(ProductSpecification specification) {
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
    
    /**
     * Convert list of ProductSpecification entities to ProductSpecificationResponse list
     */
    public List<ProductSpecificationResponse> toProductSpecificationResponses(List<ProductSpecification> specifications) {
        if (specifications == null) {
            return List.of();
        }
        
        return specifications.stream()
            .map(this::toProductSpecificationResponse)
            .collect(Collectors.toList());
    }
}

