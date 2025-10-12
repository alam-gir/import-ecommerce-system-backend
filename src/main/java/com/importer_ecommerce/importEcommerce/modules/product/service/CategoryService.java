package com.importer_ecommerce.importEcommerce.modules.product.service;

import com.importer_ecommerce.importEcommerce.modules.product.entity.Category;
import com.importer_ecommerce.importEcommerce.modules.product.entity.CategoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Service interface for Category management
 */
public interface CategoryService {
    
    /**
     * Create a new category
     */
    boolean createCategory(String title, String description, MultipartFile image);
    
    /**
     * Update an existing category
     */
    boolean updateCategory(UUID categoryId, String title, String description, MultipartFile image, CategoryStatus status);
    
    /**
     * Get category by ID
     */
    Category getCategoryById(UUID categoryId);
    
    /**
     * Get all categories with pagination and filtering
     */
    Page<Category> getAllCategories(Pageable pageable, String search, CategoryStatus status);
    
    /**
     * Delete category by ID
     */
    boolean deleteCategory(UUID categoryId);
    
    /**
     * Update category status only
     */
    boolean updateCategoryStatus(UUID categoryId, CategoryStatus status);
    
    /**
     * Check if category title exists (excluding current category)
     */
    boolean existsByTitle(String title, UUID excludeId);
}
