package com.importer_ecommerce.importEcommerce.modules.product.mapper;

import com.importer_ecommerce.importEcommerce.modules.product.dto.response.VariantAttributeResponse;
import com.importer_ecommerce.importEcommerce.modules.product.dto.response.VariantAttributeValueResponse;
import com.importer_ecommerce.importEcommerce.modules.product.entity.VariantAttribute;
import com.importer_ecommerce.importEcommerce.modules.product.entity.VariantAttributeValue;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting VariantAttribute entities to response DTOs
 * Handles attribute and attribute value conversions
 */
@Component
public class VariantAttributeMapper {
    
    /**
     * Convert VariantAttribute entity to VariantAttributeResponse
     * Includes all attribute values
     */
    public VariantAttributeResponse toVariantAttributeResponse(VariantAttribute attribute) {
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
     * Convert VariantAttributeValue entity to VariantAttributeValueResponse
     */
    public VariantAttributeValueResponse toVariantAttributeValueResponse(VariantAttributeValue value) {
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
     * Convert list of VariantAttribute entities to VariantAttributeResponse list
     */
    public List<VariantAttributeResponse> toVariantAttributeResponses(List<VariantAttribute> attributes) {
        if (attributes == null) {
            return List.of();
        }
        
        return attributes.stream()
            .map(this::toVariantAttributeResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Convert list of VariantAttributeValue entities to VariantAttributeValueResponse list
     */
    public List<VariantAttributeValueResponse> toVariantAttributeValueResponses(List<VariantAttributeValue> values) {
        if (values == null) {
            return List.of();
        }
        
        return values.stream()
            .map(this::toVariantAttributeValueResponse)
            .collect(Collectors.toList());
    }
}

