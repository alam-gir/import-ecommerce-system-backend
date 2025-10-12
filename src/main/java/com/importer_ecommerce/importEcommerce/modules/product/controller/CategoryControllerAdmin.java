package com.importer_ecommerce.importEcommerce.modules.product.controller;

import com.importer_ecommerce.importEcommerce.common.dto.response.ApiResponse;
import com.importer_ecommerce.importEcommerce.common.dto.response.PaginationResponse;
import com.importer_ecommerce.importEcommerce.common.exception.ApiException;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.common.util.RequestUtil;
import com.importer_ecommerce.importEcommerce.modules.product.dto.request.CreateCategoryRequest;
import com.importer_ecommerce.importEcommerce.modules.product.dto.request.UpdateCategoryRequest;
import com.importer_ecommerce.importEcommerce.modules.product.dto.response.CategoryResponse;
import com.importer_ecommerce.importEcommerce.modules.product.entity.Category;
import com.importer_ecommerce.importEcommerce.modules.product.entity.CategoryStatus;
import com.importer_ecommerce.importEcommerce.modules.product.mapper.CategoryMapper;
import com.importer_ecommerce.importEcommerce.modules.product.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Admin controller for Category management
 */
@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryControllerAdmin {
    
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    
    /**
     * Create a new category
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Boolean>> createCategory(@Valid @ModelAttribute CreateCategoryRequest request) {
        try {
            boolean success = categoryService.createCategory(
                request.getTitle(),
                request.getDescription(),
                request.getImage()
            );
            return RequestUtil.success("Category created successfully", success);
        } catch (ApiException e) {
            log.error("Category creation failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Unexpected error during category creation", e);
            return RequestUtil.internalError("Failed to create category. Please try again.");
        }
    }
    
    /**
     * Update an existing category
     */
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Boolean>> updateCategory(
            @PathVariable UUID categoryId,
            @Valid @ModelAttribute UpdateCategoryRequest request) {
        try {
            boolean success = categoryService.updateCategory(
                categoryId,
                request.getTitle(),
                request.getDescription(),
                request.getImage(),
                request.getStatus()
            );
            return RequestUtil.success("Category updated successfully", success);
        } catch (NotFoundException e) {
            log.error("Category not found: {}", categoryId);
            return RequestUtil.notFound(e.getMessage());
        } catch (ApiException e) {
            log.error("Category update failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Unexpected error during category update", e);
            return RequestUtil.internalError("Failed to update category. Please try again.");
        }
    }
    
    /**
     * Get category by ID
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable UUID categoryId) {
        try {
            Category category = categoryService.getCategoryById(categoryId);
            CategoryResponse response = categoryMapper.toCategoryResponse(category);
            return RequestUtil.success("Category retrieved successfully", response);
        } catch (NotFoundException e) {
            log.error("Category not found: {}", categoryId);
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during category retrieval", e);
            return RequestUtil.internalError("Failed to retrieve category. Please try again.");
        }
    }
    
    /**
     * Get all categories with pagination and filtering
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) CategoryStatus status) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Category> categoryPage = categoryService.getAllCategories(pageable, search, status);
            
            List<CategoryResponse> categories = categoryPage.getContent()
                .stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
            
            PaginationResponse pagination = new PaginationResponse(
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.hasNext(),
                categoryPage.hasPrevious()
            );
            
            return RequestUtil.success("Categories retrieved successfully", categories, pagination);
        } catch (Exception e) {
            log.error("Unexpected error during categories retrieval", e);
            return RequestUtil.internalError("Failed to retrieve categories. Please try again.");
        }
    }
    
    /**
     * Delete category by ID
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteCategory(@PathVariable UUID categoryId) {
        try {
            boolean success = categoryService.deleteCategory(categoryId);
            return RequestUtil.success("Category deleted successfully", success);
        } catch (NotFoundException e) {
            log.error("Category not found: {}", categoryId);
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during category deletion", e);
            return RequestUtil.internalError("Failed to delete category. Please try again.");
        }
    }
    
    /**
     * Update category status only
     */
    @PutMapping("/{categoryId}/status")
    public ResponseEntity<ApiResponse<Boolean>> updateCategoryStatus(
            @PathVariable UUID categoryId,
            @RequestParam("value") CategoryStatus status) {
        try {
            boolean success = categoryService.updateCategoryStatus(categoryId, status);
            return RequestUtil.success("Category status updated successfully", success);
        } catch (NotFoundException e) {
            log.error("Category not found: {}", categoryId);
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during category status update", e);
            return RequestUtil.internalError("Failed to update category status. Please try again.");
        }
    }
}
