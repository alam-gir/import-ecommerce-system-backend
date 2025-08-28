package com.importer_ecommerce.importEcommerce.product.service;

import com.importer_ecommerce.importEcommerce.product.dto.request.CreateCategoryRequest;
import com.importer_ecommerce.importEcommerce.product.dto.request.UpdateCategoryRequest;
import com.importer_ecommerce.importEcommerce.product.dto.response.CategoryResponse;
import com.importer_ecommerce.importEcommerce.product.dto.response.CategoryListResponse;

import java.util.UUID;

public interface CategoryService {
    
    CategoryResponse createCategory(CreateCategoryRequest request);
    
    CategoryResponse getCategoryById(UUID id);
    
    CategoryListResponse getAllCategories(int page, int size);
    
    CategoryListResponse getRootCategories();
    
    CategoryListResponse getChildrenCategories(UUID parentId);
    
    CategoryListResponse searchCategories(String searchTerm, int page, int size);
    
    CategoryResponse updateCategory(UUID id, UpdateCategoryRequest request);
    
    void deleteCategory(UUID id);
    
    boolean existsById(UUID id);
}
