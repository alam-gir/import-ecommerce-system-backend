package com.importer_ecommerce.importEcommerce.product.mapper;

import com.importer_ecommerce.importEcommerce.product.dto.request.CreateCategoryRequest;
import com.importer_ecommerce.importEcommerce.product.dto.request.UpdateCategoryRequest;
import com.importer_ecommerce.importEcommerce.product.dto.response.CategoryResponse;
import com.importer_ecommerce.importEcommerce.product.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {
    
    public Category toEntity(CreateCategoryRequest request) {
        Category category = new Category();
        category.setTitle(request.getTitle());
        category.setDescription(request.getDescription());
        return category;
    }
    
    public void updateEntityFromRequest(UpdateCategoryRequest request, Category category) {
        if (request.getTitle() != null) {
            category.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
    }
    
    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getTitle(),
            category.getDescription(),
            category.getProfilePicture(),
            category.getCoverImage(),
            category.getParent() != null ? category.getParent().getId() : null,
            category.getParent() != null ? category.getParent().getTitle() : null,
            category.getChildren() != null ? 
                category.getChildren().stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList()) : 
                List.of(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }
    
    public List<CategoryResponse> toResponseList(List<Category> categories) {
        return categories.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}
