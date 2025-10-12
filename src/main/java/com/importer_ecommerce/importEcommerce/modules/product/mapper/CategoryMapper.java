package com.importer_ecommerce.importEcommerce.modules.product.mapper;

import com.importer_ecommerce.importEcommerce.modules.product.dto.response.CategoryResponse;
import com.importer_ecommerce.importEcommerce.modules.product.entity.Category;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting Category entities to response DTOs
 */
@Component
public class CategoryMapper {
    
    /**
     * Convert Category entity to CategoryResponse
     */
    public CategoryResponse toCategoryResponse(Category category) {
        if (category == null) {
            return null;
        }
        
        return new CategoryResponse(
            category.getId(),
            category.getTitle(),
            category.getImage(),
            category.getDescription(),
            category.getStatus(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }
}
