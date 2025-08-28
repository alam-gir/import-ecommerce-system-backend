package com.importer_ecommerce.importEcommerce.product.mapper;

import com.importer_ecommerce.importEcommerce.product.dto.response.ProductCategoryResponse;
import com.importer_ecommerce.importEcommerce.product.dto.response.ProductResponse;
import com.importer_ecommerce.importEcommerce.product.entity.Category;
import com.importer_ecommerce.importEcommerce.product.entity.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {
    
    public ProductResponse toResponse(Product product) {
        List<ProductCategoryResponse> categoryResponses = null;
        if (product.getCategories() != null) {
            categoryResponses = product.getCategories().stream()
                .map(this::toProductCategoryResponse)
                .collect(Collectors.toList());
        }
        
        return new ProductResponse(
            product.getId(),
            product.getTitle(),
            product.getSlug(),
            product.getDescription(),
            product.getMediaDescriptions(),
            product.getNote(),
            product.getBrand(),
            product.getAttributes(),
            product.getStatus(),
            product.getFeatured(),
            product.getSeoTitle(),
            product.getSeoDescription(),
            product.getSeoKeywords(),
            categoryResponses,
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
    
    private ProductCategoryResponse toProductCategoryResponse(Category category) {
        return new ProductCategoryResponse(
            category.getId(),
            category.getTitle(),
            category.getDescription(),
            category.getProfilePicture(),
            category.getCoverImage()
        );
    }
}
