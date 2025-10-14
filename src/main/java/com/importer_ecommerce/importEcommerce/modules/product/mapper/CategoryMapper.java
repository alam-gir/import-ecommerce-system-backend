package com.importer_ecommerce.importEcommerce.modules.product.mapper;

import com.importer_ecommerce.importEcommerce.modules.product.dto.response.CategoryResponse;
import com.importer_ecommerce.importEcommerce.modules.product.dto.response.CategorySimpleResponse;
import com.importer_ecommerce.importEcommerce.modules.product.dto.response.CategoryHierarchicalResponse;
import com.importer_ecommerce.importEcommerce.modules.product.dto.response.CategoryOptionResponse;
import com.importer_ecommerce.importEcommerce.modules.product.dto.response.CategoryTreeResponse;
import com.importer_ecommerce.importEcommerce.modules.product.entity.Category;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting Category entities to response DTOs
 */
@Component
public class CategoryMapper {
    
    /**
     * Convert Category entity to CategorySimpleResponse (without children)
     */
    public CategorySimpleResponse toCategorySimpleResponse(Category category) {
        if (category == null) {
            return null;
        }
        
        return new CategorySimpleResponse(
            category.getId(),
            category.getTitle(),
            category.getImage(),
            category.getDescription(),
            category.getStatus(),
            category.getParent() != null ? category.getParent().getId() : null,
            category.getParent() != null ? category.getParent().getTitle() : null,
            category.getLevel(),
            category.hasChildren(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }
    
    /**
     * Convert Category entity to CategoryResponse (with children)
     */
    public CategoryResponse toCategoryResponse(Category category) {
        if (category == null) {
            return null;
        }
        
        List<CategoryResponse> children = new ArrayList<>();
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            children = category.getChildren().stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());
        }
        
        return new CategoryResponse(
            category.getId(),
            category.getTitle(),
            category.getImage(),
            category.getDescription(),
            category.getStatus(),
            category.getParent() != null ? category.getParent().getId() : null,
            category.getParent() != null ? category.getParent().getTitle() : null,
            children,
            category.getLevel(),
            category.hasChildren(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }
    
    /**
     * Convert Category entity to CategoryHierarchicalResponse (for hierarchical list - no children field)
     */
    public CategoryHierarchicalResponse toCategoryHierarchicalResponse(Category category) {
        if (category == null) {
            return null;
        }
        
        return new CategoryHierarchicalResponse(
            category.getId(),
            category.getTitle(),
            category.getImage(),
            category.getDescription(),
            category.getStatus(),
            category.getParent() != null ? category.getParent().getId() : null,
            category.getParent() != null ? category.getParent().getTitle() : null,
            category.getLevel(),
            category.hasChildren(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }
    
    /**
     * Convert Category entity to CategoryOptionResponse (minimal data for UI select dropdowns)
     */
    public CategoryOptionResponse toCategoryOptionResponse(Category category) {
        if (category == null) {
            return null;
        }
        
        return new CategoryOptionResponse(
            category.getId(),
            category.getTitle()
        );
    }
    
    /**
     * Convert Category entity to CategoryTreeResponse (tree structure with children)
     */
    public CategoryTreeResponse toCategoryTreeResponse(Category category) {
        if (category == null) {
            return null;
        }
        
        List<CategoryTreeResponse> children = new ArrayList<>();
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            children = category.getChildren().stream()
                .map(this::toCategoryTreeResponse)
                .collect(Collectors.toList());
        }
        
        return new CategoryTreeResponse(
            category.getId(),
            category.getTitle(),
            children
        );
    }
    
    /**
     * Convert list of root categories to tree structure
     */
    public List<CategoryTreeResponse> toCategoryTreeResponseList(List<Category> rootCategories) {
        if (rootCategories == null) {
            return new ArrayList<>();
        }
        
        return rootCategories.stream()
            .map(this::toCategoryTreeResponse)
            .collect(Collectors.toList());
    }
}
