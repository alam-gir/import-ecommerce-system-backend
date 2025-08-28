package com.importer_ecommerce.importEcommerce.product.service.impl;

import com.importer_ecommerce.importEcommerce.cloudstorage.dto.FileUploadResponse;
import com.importer_ecommerce.importEcommerce.cloudstorage.service.CloudflareR2Service;
import com.importer_ecommerce.importEcommerce.common.exception.ConflictException;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.product.dto.request.CreateCategoryRequest;
import com.importer_ecommerce.importEcommerce.product.dto.request.UpdateCategoryRequest;
import com.importer_ecommerce.importEcommerce.product.dto.response.CategoryResponse;
import com.importer_ecommerce.importEcommerce.product.dto.response.CategoryListResponse;
import com.importer_ecommerce.importEcommerce.product.entity.Category;
import com.importer_ecommerce.importEcommerce.product.mapper.CategoryMapper;
import com.importer_ecommerce.importEcommerce.product.repository.CategoryRepository;
import com.importer_ecommerce.importEcommerce.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CloudflareR2Service cloudflareR2Service;
    
    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        log.info("Creating category with title: {}", request.getTitle());
        
        // Check if category with same title already exists (in the same parent level)
        if (request.getParentId() != null) {
            // Check for duplicate title among siblings
            if (categoryRepository.existsByTitleAndIdNot(request.getTitle(), UUID.randomUUID())) {
                throw new ConflictException("Category with title '" + request.getTitle() + "' already exists at this level");
            }
        } else {
            // Check for duplicate title among root categories
            if (categoryRepository.existsByTitleAndIdNot(request.getTitle(), UUID.randomUUID())) {
                throw new ConflictException("Category with title '" + request.getTitle() + "' already exists at root level");
            }
        }
        
        Category category = categoryMapper.toEntity(request);
        
        // Set parent if provided
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                .orElseThrow(() -> new NotFoundException("Parent Category", request.getParentId().toString()));
            
            category.setParent(parent);
        }
        
        // Handle profile image upload
        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            try {
                FileUploadResponse profileResponse = cloudflareR2Service.uploadFile(request.getProfileImage(), "categories");
                category.setProfilePicture(profileResponse.getFileUrl());
                log.info("Profile image uploaded successfully for category: {}", request.getTitle());
            } catch (Exception e) {
                log.error("Failed to upload profile image for category: {}", request.getTitle(), e);
                // Continue without profile image
            }
        }
        
        // Handle cover image upload
        if (request.getCoverImage() != null && !request.getCoverImage().isEmpty()) {
            try {
                FileUploadResponse coverResponse = cloudflareR2Service.uploadFile(request.getCoverImage(), "categories");
                category.setCoverImage(coverResponse.getFileUrl());
                log.info("Cover image uploaded successfully for category: {}", request.getTitle());
            } catch (Exception e) {
                log.error("Failed to upload cover image for category: {}", request.getTitle(), e);
                // Continue without cover image
            }
        }
        
        Category savedCategory = categoryRepository.save(category);
        
        log.info("Category created successfully with ID: {}", savedCategory.getId());
        return categoryMapper.toResponse(savedCategory);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID id) {
        log.info("Fetching category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Category", id.toString()));
        
        return categoryMapper.toResponse(category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CategoryListResponse getAllCategories(int page, int size) {
        log.info("Fetching all categories with page: {} and size: {}", page, size);
        
        List<Category> categories = categoryRepository.findAll();
        
        // Manual pagination
        int start = page * size;
        int end = Math.min(start + size, categories.size());
        
        List<Category> paginatedCategories = categories.subList(start, end);
        List<CategoryResponse> categoryResponses = categoryMapper.toResponseList(paginatedCategories);
        
        return new CategoryListResponse(categoryResponses, categories.size());
    }
    
    @Override
    @Transactional(readOnly = true)
    public CategoryListResponse getRootCategories() {
        log.info("Fetching root categories");
        
        List<Category> rootCategories = categoryRepository.findByParentIsNull();
        List<CategoryResponse> categoryResponses = categoryMapper.toResponseList(rootCategories);
        
        return new CategoryListResponse(categoryResponses, rootCategories.size());
    }
    
    @Override
    @Transactional(readOnly = true)
    public CategoryListResponse getChildrenCategories(UUID parentId) {
        log.info("Fetching children categories for parent ID: {}", parentId);
        
        // Verify parent exists
        if (!categoryRepository.existsById(parentId)) {
            throw new NotFoundException("Parent Category", parentId.toString());
        }
        
        List<Category> childrenCategories = categoryRepository.findByParentId(parentId);
        List<CategoryResponse> categoryResponses = categoryMapper.toResponseList(childrenCategories);
        
        return new CategoryListResponse(categoryResponses, childrenCategories.size());
    }
    
    @Override
    @Transactional(readOnly = true)
    public CategoryListResponse searchCategories(String searchTerm, int page, int size) {
        log.info("Searching categories with term: '{}', page: {} and size: {}", searchTerm, page, size);
        
        List<Category> categories = categoryRepository.findByTitleOrDescriptionContaining(searchTerm);
        
        // Manual pagination for search results
        int start = page * size;
        int end = Math.min(start + size, categories.size());
        
        List<Category> paginatedCategories = categories.subList(start, end);
        List<CategoryResponse> categoryResponses = categoryMapper.toResponseList(paginatedCategories);
        
        return new CategoryListResponse(categoryResponses, categories.size());
    }
    
    @Override
    public CategoryResponse updateCategory(UUID id, UpdateCategoryRequest request) {
        log.info("Updating category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Category", id.toString()));
        
        // Check if new title conflicts with existing category at the same level
        if (request.getTitle() != null && !request.getTitle().equals(category.getTitle())) {
            UUID currentParentId = category.getParent() != null ? category.getParent().getId() : null;
            UUID newParentId = request.getParentId() != null ? request.getParentId() : null;
            
            // Check for conflicts at the new level
            if (categoryRepository.existsByTitleAndIdNot(request.getTitle(), id)) {
                throw new ConflictException("Category with title '" + request.getTitle() + "' already exists at this level");
            }
        }
        
        // Update text fields
        categoryMapper.updateEntityFromRequest(request, category);
        
        // Update parent if provided
        if (request.getParentId() != null) {
            Category newParent = categoryRepository.findById(request.getParentId())
                .orElseThrow(() -> new NotFoundException("Parent Category", request.getParentId().toString()));
            
            // Prevent circular reference
            if (newParent.getId().equals(id)) {
                throw new ConflictException("Category cannot be its own parent");
            }
            
            // Prevent setting parent to one of its own children
            if (isDescendant(newParent, id)) {
                throw new ConflictException("Cannot set parent to a descendant category");
            }
            
            category.setParent(newParent);
        } else if (request.getParentId() == null && category.getParent() != null) {
            // Remove parent (make it root)
            category.setParent(null);
        }
        
        // Handle profile image upload
        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            try {
                // Delete old profile image if exists
                if (category.getProfilePicture() != null) {
                    cloudflareR2Service.deleteFile(category.getProfilePicture());
                }
                
                FileUploadResponse profileResponse = cloudflareR2Service.uploadFile(request.getProfileImage(), "categories");
                category.setProfilePicture(profileResponse.getFileUrl());
                log.info("Profile image updated successfully for category ID: {}", id);
            } catch (Exception e) {
                log.error("Failed to update profile image for category ID: {}", id, e);
                // Continue without updating profile image
            }
        }
        
        // Handle cover image upload
        if (request.getCoverImage() != null && !request.getCoverImage().isEmpty()) {
            try {
                // Delete old cover image if exists
                if (category.getCoverImage() != null) {
                    cloudflareR2Service.deleteFile(category.getCoverImage());
                }
                
                FileUploadResponse coverResponse = cloudflareR2Service.uploadFile(request.getCoverImage(), "categories");
                category.setCoverImage(coverResponse.getFileUrl());
                log.info("Cover image updated successfully for category ID: {}", id);
            } catch (Exception e) {
                log.error("Failed to update cover image for category ID: {}", id, e);
                // Continue without updating cover image
            }
        }
        
        Category updatedCategory = categoryRepository.save(category);
        
        log.info("Category updated successfully with ID: {}", updatedCategory.getId());
        return categoryMapper.toResponse(updatedCategory);
    }
    
    @Override
    public void deleteCategory(UUID id) {
        log.info("Deleting category with ID: {}", id);
        
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Category", id.toString());
        }
        
        // Check if category has children
        if (categoryRepository.existsByParentId(id)) {
            throw new ConflictException("Cannot delete category with children. Please delete children first or reassign them.");
        }
        
        // Get category to delete associated images
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            // Delete profile image if exists
            if (category.getProfilePicture() != null) {
                try {
                    cloudflareR2Service.deleteFile(category.getProfilePicture());
                    log.info("Profile image deleted for category ID: {}", id);
                } catch (Exception e) {
                    log.error("Failed to delete profile image for category ID: {}", id, e);
                }
            }
            
            // Delete cover image if exists
            if (category.getCoverImage() != null) {
                try {
                    cloudflareR2Service.deleteFile(category.getCoverImage());
                    log.info("Cover image deleted for category ID: {}", id);
                } catch (Exception e) {
                    log.error("Failed to delete cover image for category ID: {}", id, e);
                }
            }
        }
        
        categoryRepository.deleteById(id);
        log.info("Category deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return categoryRepository.existsById(id);
    }
    
    /**
     * Check if a category is a descendant of another category
     */
    private boolean isDescendant(Category potentialDescendant, UUID ancestorId) {
        if (potentialDescendant == null) {
            return false;
        }
        
        Category current = potentialDescendant.getParent();
        while (current != null) {
            if (current.getId().equals(ancestorId)) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }
}
