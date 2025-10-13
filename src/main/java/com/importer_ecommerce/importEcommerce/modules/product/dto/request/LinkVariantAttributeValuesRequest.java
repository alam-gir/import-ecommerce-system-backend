package com.importer_ecommerce.importEcommerce.modules.product.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * Request DTO for linking attribute values to a product variant
 * Used to update which attribute values are associated with a variant
 */
@Data
public class LinkVariantAttributeValuesRequest {
    
    @NotEmpty(message = "Attribute value IDs are required")
    private List<String> attributeValueIds;
}
