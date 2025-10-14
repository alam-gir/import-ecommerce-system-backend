package com.importer_ecommerce.importEcommerce.modules.product.service.impl;

import com.importer_ecommerce.importEcommerce.cloudflare.service.CloudflareService;
import com.importer_ecommerce.importEcommerce.common.exception.ApiException;
import com.importer_ecommerce.importEcommerce.common.exception.ConflictException;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.modules.product.entity.Category;
import com.importer_ecommerce.importEcommerce.modules.product.entity.CategoryStatus;
import com.importer_ecommerce.importEcommerce.modules.product.repository.CategoryRepository;
import com.importer_ecommerce.importEcommerce.modules.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Implementation of CategoryService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final CloudflareService cloudflareService;
    
    @Override
    public Category createCategory(String title, String description, MultipartFile image, UUID parentId) {
        // Check if title already exists
        if (existsByTitle(title, null)) {
            throw new ConflictException("Category", "title", title);
        }
        
        // Validate parent category exists if provided
        Category parent = null;
        if (parentId != null) {
            parent = getCategoryById(parentId);
        }
        
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = cloudflareService.uploadFile(image, "categories");
                log.info("Category image uploaded to Cloudflare: {}", imageUrl);
            } catch (Exception e) {
                log.error("Failed to upload category image: {}", e.getMessage());
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload category image: " + e.getMessage());
            }
        }
        
        Category category = Category.builder()
            .title(title)
            .description(description)
            .image(imageUrl)
            .parent(parent)
            .status(CategoryStatus.ACTIVE)
            .build();
        
        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully: {}", title);
        return savedCategory;
    }
    
    @Override
    public Category updateCategory(UUID categoryId, String title, String description, MultipartFile image, CategoryStatus status, UUID parentId) {
        Category category = getCategoryById(categoryId);
        
        // Check if title already exists (excluding current category)
        if (!category.getTitle().equals(title) && existsByTitle(title, categoryId)) {
            throw new ConflictException("Category", "title", title);
        }
        
        String oldImageUrl = category.getImage();
        String newImageUrl = oldImageUrl;
        
        // Handle image update
        if (image != null && !image.isEmpty()) {
            try {
                newImageUrl = cloudflareService.uploadFile(image, "categories");
                log.info("New category image uploaded to Cloudflare: {}", newImageUrl);
                
                // Delete old image if it exists
                if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                    try {
                        cloudflareService.deleteFile(oldImageUrl);
                        log.info("Old category image deleted from Cloudflare: {}", oldImageUrl);
                    } catch (Exception e) {
                        log.warn("Failed to delete old category image: {}", e.getMessage());
                    }
                }
            } catch (Exception e) {
                log.error("Failed to upload new category image: {}", e.getMessage());
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload new category image: " + e.getMessage());
            }
        }
        
        // Handle parent update
        Category currentParent = category.getParent();
        UUID currentParentId = currentParent != null ? currentParent.getId() : null;
        
        // Only update parent if it's different
        if (!java.util.Objects.equals(currentParentId, parentId)) {
            // Validate parent category exists if provided
            Category newParent = null;
            if (parentId != null) {
                newParent = getCategoryById(parentId);
                
                // Prevent circular reference
                if (newParent.getId().equals(categoryId)) {
                    throw new ConflictException("Category cannot be its own parent");
                }
                
                // Check if parent is a descendant of current category
                if (isDescendant(newParent, category)) {
                    throw new ConflictException("Cannot set parent: would create circular reference");
                }
            }
            
            category.setParent(newParent);
            log.info("Category parent updated: {} -> parent: {}", category.getTitle(), newParent != null ? newParent.getTitle() : "None");
        }
        
        category.setTitle(title);
        category.setDescription(description);
        category.setImage(newImageUrl);
        if (status != null) {
            category.setStatus(status);
        }
        
        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated successfully: {}", title);
        return updatedCategory;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new NotFoundException("Category", categoryId.toString()));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Category getCategoryByTitle(String title) {
        return categoryRepository.findByTitleIgnoreCase(title)
            .orElseThrow(() -> new NotFoundException("Category", "title", title));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Category> getAllCategories(Pageable pageable, String search, CategoryStatus status, Integer level) {
        // Handle level filtering with a simpler approach
        if (level != null) {
            if (level == 0) {
                // Level 0 = Root categories only
                return categoryRepository.findRootCategoriesPage(pageable);
            } else {
                // For other levels, we'll filter in memory for now
                // This is not ideal for large datasets but will work for debugging
                List<Category> allCategories = categoryRepository.findAll();
                List<Category> filteredCategories = allCategories.stream()
                    .filter(cat -> cat.getLevel() == level)
                    .collect(Collectors.toList());
                
                // Apply other filters
                if (search != null && !search.trim().isEmpty()) {
                    filteredCategories = filteredCategories.stream()
                        .filter(cat -> cat.getTitle().toLowerCase().contains(search.trim().toLowerCase()))
                        .collect(Collectors.toList());
                }
                
                if (status != null) {
                    filteredCategories = filteredCategories.stream()
                        .filter(cat -> cat.getStatus() == status)
                        .collect(Collectors.toList());
                }
                
                // Convert to Page manually (simplified approach)
                int start = (int) pageable.getOffset();
                int end = Math.min((start + pageable.getPageSize()), filteredCategories.size());
                List<Category> pageContent = start < filteredCategories.size() ? 
                    filteredCategories.subList(start, end) : new ArrayList<>();
                
                return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, filteredCategories.size());
            }
        }
        
        // Original logic when level is not specified
        if (search != null && !search.trim().isEmpty() && status != null) {
            return categoryRepository.findByTitleContainingIgnoreCaseAndStatus(search.trim(), status, pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            return categoryRepository.findByTitleContainingIgnoreCase(search.trim(), pageable);
        } else if (status != null) {
            return categoryRepository.findByStatus(status, pageable);
        } else {
            return categoryRepository.findAll(pageable);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getRootCategories() {
        return categoryRepository.findRootCategories();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getChildCategories(UUID parentId) {
        return categoryRepository.findByParentId(parentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategoriesHierarchical() {
        return categoryRepository.findAllHierarchical();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Category getCategoryHierarchy(UUID categoryId) {
        return categoryRepository.findByIdWithParentAndChildren(categoryId)
            .orElseThrow(() -> new NotFoundException("Category not found with ID: " + categoryId));
    }
    
    @Override
    @Transactional
    public Category updateCategoryParent(UUID categoryId, UUID parentId) {
        Category category = getCategoryById(categoryId);
        
        // Validate parent category exists if provided
        Category parent = null;
        if (parentId != null) {
            parent = getCategoryById(parentId);
            
            // Prevent circular reference
            if (parent.getId().equals(categoryId)) {
                throw new ConflictException("Category cannot be its own parent");
            }
            
            // Check if parent is a descendant of current category
            if (isDescendant(parent, category)) {
                throw new ConflictException("Cannot set parent: would create circular reference");
            }
        }
        
        category.setParent(parent);
        Category updatedCategory = categoryRepository.save(category);
        
        log.info("Category parent updated successfully: {} -> parent: {}", category.getTitle(), parent != null ? parent.getTitle() : "None");
        return updatedCategory;
    }
    
    @Override
    @Transactional
    public Category updateCategoryStatus(UUID categoryId, CategoryStatus status) {
        Category category = getCategoryById(categoryId);
        
        category.setStatus(status);
        Category updatedCategory = categoryRepository.save(category);
        
        log.info("Category status updated successfully: {} -> {}", category.getTitle(), status);
        return updatedCategory;
    }
    
    @Override
    @Transactional
    public void deleteCategory(UUID categoryId) {
        Category category = getCategoryById(categoryId);
        
        // Delete image from Cloudflare if exists
        if (category.getImage() != null && !category.getImage().isEmpty()) {
            try {
                cloudflareService.deleteFile(category.getImage());
                log.info("Category image deleted from Cloudflare: {}", category.getImage());
            } catch (Exception e) {
                log.warn("Failed to delete category image from Cloudflare: {}", e.getMessage());
            }
        }
        
        // Hard delete the category
        categoryRepository.delete(category);
        
        log.info("Category deleted successfully: {}", category.getTitle());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByTitle(String title, UUID excludeId) {
        return categoryRepository.existsByTitleAndNotDeleted(title, excludeId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getParentOptions(UUID categoryId) {
        // Get the category to find its descendants
        Category category = getCategoryById(categoryId);
        
        // Get all categories
        List<Category> allCategories = categoryRepository.findAll();
        
        // Get all descendant IDs (children, grandchildren, etc.)
        Set<UUID> descendantIds = getAllDescendantIds(category);
        
        // Filter out self and descendants
        return allCategories.stream()
            .filter(cat -> !cat.getId().equals(categoryId)) // Exclude self
            .filter(cat -> !descendantIds.contains(cat.getId())) // Exclude descendants
            .sorted(Comparator.comparing(Category::getTitle)) // Sort by title
            .collect(Collectors.toList());
    }
    
    /**
     * Get all descendant IDs recursively (children, grandchildren, etc.)
     */
    private Set<UUID> getAllDescendantIds(Category category) {
        Set<UUID> descendantIds = new HashSet<>();
        collectDescendantIds(category, descendantIds);
        return descendantIds;
    }
    
    /**
     * Recursively collect all descendant IDs
     */
    private void collectDescendantIds(Category category, Set<UUID> descendantIds) {
        if (category.getChildren() != null) {
            for (Category child : category.getChildren()) {
                descendantIds.add(child.getId());
                collectDescendantIds(child, descendantIds); // Recursive call for grandchildren
            }
        }
    }
    
    /**
     * Check if a category is a descendant of another category
     */
    private boolean isDescendant(Category potentialDescendant, Category ancestor) {
        if (potentialDescendant == null || ancestor == null) {
            return false;
        }
        
        Category current = potentialDescendant.getParent();
        while (current != null) {
            if (current.getId().equals(ancestor.getId())) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Integer getMaxLevel() {
        // Use a simpler approach - calculate max level in memory
        List<Category> allCategories = categoryRepository.findAll();
        int maxLevel = 0;
        
        for (Category category : allCategories) {
            int level = category.getLevel();
            if (level > maxLevel) {
                maxLevel = level;
            }
        }
        
        return maxLevel;
    }
}
