package com.importer_ecommerce.importEcommerce.modules.product.service.impl;

import com.importer_ecommerce.importEcommerce.cloudflare.service.CloudflareService;
import com.importer_ecommerce.importEcommerce.common.exception.ConflictException;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.modules.product.entity.Category;
import com.importer_ecommerce.importEcommerce.modules.product.entity.CategoryStatus;
import com.importer_ecommerce.importEcommerce.modules.product.repository.CategoryRepository;
import com.importer_ecommerce.importEcommerce.modules.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

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
    public boolean createCategory(String title, String description, MultipartFile image) {
        // Check if title already exists
        if (existsByTitle(title, null)) {
            throw new ConflictException("Category", "title", title);
        }
        
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = cloudflareService.uploadFile(image, "categories");
                log.info("Category image uploaded to Cloudflare: {}", imageUrl);
            } catch (Exception e) {
                log.error("Failed to upload category image: {}", e.getMessage());
                throw new RuntimeException("Failed to upload image", e);
            }
        }
        
        Category category = Category.builder()
            .title(title)
            .description(description)
            .image(imageUrl)
            .status(CategoryStatus.ACTIVE)
            .build();
        
        categoryRepository.save(category);
        log.info("Category created successfully: {}", title);
        return true;
    }
    
    @Override
    public boolean updateCategory(UUID categoryId, String title, String description, MultipartFile image, CategoryStatus status) {
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
                throw new RuntimeException("Failed to upload image", e);
            }
        }
        
        category.setTitle(title);
        category.setDescription(description);
        category.setImage(newImageUrl);
        category.setStatus(status);
        
        categoryRepository.save(category);
        log.info("Category updated successfully: {}", title);
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(UUID categoryId) {
        return categoryRepository.findByIdAndNotDeleted(categoryId)
            .orElseThrow(() -> new NotFoundException("Category", categoryId.toString()));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Category> getAllCategories(Pageable pageable, String search, CategoryStatus status) {
        if (search != null && !search.trim().isEmpty() && status != null) {
            return categoryRepository.findByTitleContainingIgnoreCaseAndStatusAndNotDeleted(search.trim(), status, pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            return categoryRepository.findByTitleContainingIgnoreCaseAndNotDeleted(search.trim(), pageable);
        } else if (status != null) {
            return categoryRepository.findByStatusAndNotDeleted(status, pageable);
        } else {
            return categoryRepository.findAllActive(pageable);
        }
    }
    
    @Override
    public boolean deleteCategory(UUID categoryId) {
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
        
        // Soft delete the category
        category.setIsDeleted(true);
        categoryRepository.save(category);
        
        log.info("Category deleted successfully: {}", category.getTitle());
        return true;
    }
    
    @Override
    public boolean updateCategoryStatus(UUID categoryId, CategoryStatus status) {
        Category category = getCategoryById(categoryId);
        
        category.setStatus(status);
        categoryRepository.save(category);
        
        log.info("Category status updated successfully: {} -> {}", category.getTitle(), status);
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByTitle(String title, UUID excludeId) {
        return categoryRepository.existsByTitleAndNotDeleted(title, excludeId);
    }
}
