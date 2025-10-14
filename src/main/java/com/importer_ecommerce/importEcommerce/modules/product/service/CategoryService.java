package com.importer_ecommerce.importEcommerce.modules.product.service;

import com.importer_ecommerce.importEcommerce.modules.product.entity.Category;
import com.importer_ecommerce.importEcommerce.modules.product.entity.CategoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Category management
 */
public interface CategoryService {
    
    /**
     * Create a new category
     */
    Category createCategory(String title, String description, MultipartFile image, UUID parentId);
    
    /**
     * Update an existing category
     */
    Category updateCategory(UUID categoryId, String title, String description, MultipartFile image, CategoryStatus status, UUID parentId);
    
    /**
     * Get category by ID
     */
    Category getCategoryById(UUID categoryId);
    
    /**
     * Get category by title
     */
    Category getCategoryByTitle(String title);
    
    /**
     * Get all categories with pagination and filtering
     */
    Page<Category> getAllCategories(Pageable pageable, String search, CategoryStatus status, Integer level);
    
    /**
     * Get root categories (categories with no parent)
     */
    List<Category> getRootCategories();
    
    /**
     * Get child categories by parent ID
     */
    List<Category> getChildCategories(UUID parentId);
    
    /**
     * Get all categories in hierarchical order (flat list)
     */
    List<Category> getAllCategoriesHierarchical();
    
    /**
     * Get category hierarchy by category ID (with parent and children)
     */
    Category getCategoryHierarchy(UUID categoryId);
    
    /**
     * Update category parent
     */
    Category updateCategoryParent(UUID categoryId, UUID parentId);
    
    /**
     * Update category status only
     */
    Category updateCategoryStatus(UUID categoryId, CategoryStatus status);
    
    /**
     * Delete category by ID
     */
    void deleteCategory(UUID categoryId);
    
    /**
     * Check if category title exists (excluding current category)
     */
    boolean existsByTitle(String title, UUID excludeId);

    /**
     * Get parent options for a specific category (excludes self and descendants)
     */
    List<Category> getParentOptions(UUID categoryId);
    
    /**
     * Get maximum level of categories in the database
     */
    Integer getMaxLevel();
}
