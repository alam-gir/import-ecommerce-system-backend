package com.importer_ecommerce.importEcommerce.product.mapper;

import com.importer_ecommerce.importEcommerce.product.dto.response.ProductVariantResponse;
import com.importer_ecommerce.importEcommerce.product.entity.ProductVariant;
import org.springframework.stereotype.Component;

@Component
public class ProductVariantMapper {
    
    public ProductVariantResponse toResponse(ProductVariant variant) {
        return new ProductVariantResponse(
            variant.getId(),
            variant.getSku(),
            variant.getTitle(),
            variant.getPrice(),
            variant.getCompareAtPrice(),
            variant.getInventoryQuantity(),
            variant.getInventoryTracking(),
            variant.getLowStockThreshold(),
            variant.getAttributes(),
            variant.getImages(),
            variant.getIsActive(),
            variant.getProduct().getId(),
            variant.getProduct().getTitle(),
            variant.getCreatedAt(),
            variant.getUpdatedAt()
        );
    }
}