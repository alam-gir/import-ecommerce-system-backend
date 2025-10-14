package com.importer_ecommerce.importEcommerce.modules.product.controller;

import com.importer_ecommerce.importEcommerce.common.dto.response.ApiResponse;
import com.importer_ecommerce.importEcommerce.common.dto.response.PaginationResponse;
import com.importer_ecommerce.importEcommerce.common.exception.ApiException;
import com.importer_ecommerce.importEcommerce.common.exception.ConflictException;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.common.util.RequestUtil;
import com.importer_ecommerce.importEcommerce.modules.product.dto.request.CreateCategoryRequest;
import com.importer_ecommerce.importEcommerce.modules.product.dto.request.UpdateCategoryRequest;
import com.importer_ecommerce.importEcommerce.modules.product.dto.response.CategoryResponse;
import com.importer_ecommerce.importEcommerce.modules.product.dto.response.CategorySimpleResponse;
import com.importer_ecommerce.importEcommerce.modules.product.dto.response.CategoryHierarchicalResponse;
import com.importer_ecommerce.importEcommerce.modules.product.dto.response.CategoryOptionResponse;
import com.importer_ecommerce.importEcommerce.modules.product.dto.response.CategoryTreeResponse;
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
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<CategorySimpleResponse>> createCategory(@Valid @ModelAttribute CreateCategoryRequest request) {
        try {
            Category category = categoryService.createCategory(
                request.getTitle(),
                request.getDescription(),
                request.getImage(),
                request.getParentId()
            );
            CategorySimpleResponse response = categoryMapper.toCategorySimpleResponse(category);
            return RequestUtil.success("Category created successfully", response);
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
    public ResponseEntity<ApiResponse<CategorySimpleResponse>> updateCategory(
            @PathVariable UUID categoryId,
            @Valid @ModelAttribute UpdateCategoryRequest request) {
        try {
            Category category = categoryService.updateCategory(
                categoryId,
                request.getTitle(),
                request.getDescription(),
                request.getImage(),
                request.getStatus(),
                request.getParentId()
            );
            CategorySimpleResponse response = categoryMapper.toCategorySimpleResponse(category);
            return RequestUtil.success("Category updated successfully", response);
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
    public ResponseEntity<ApiResponse<CategorySimpleResponse>> getCategoryById(@PathVariable UUID categoryId) {
        try {
            Category category = categoryService.getCategoryById(categoryId);
            CategorySimpleResponse response = categoryMapper.toCategorySimpleResponse(category);
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
     * Get category by title
     */
    @GetMapping("/by-title/{title}")
    public ResponseEntity<ApiResponse<CategorySimpleResponse>> getCategoryByTitle(@PathVariable String title) {
        try {
            Category category = categoryService.getCategoryByTitle(title);
            CategorySimpleResponse response = categoryMapper.toCategorySimpleResponse(category);
            return RequestUtil.success("Category retrieved successfully", response);
        } catch (NotFoundException e) {
            log.error("Category not found: {}", title);
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
    public ResponseEntity<ApiResponse<List<CategorySimpleResponse>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) CategoryStatus status,
            @RequestParam(required = false) Integer level) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Category> categoryPage = categoryService.getAllCategories(pageable, search, status, level);
            
            List<CategorySimpleResponse> categories = categoryPage.getContent()
                .stream()
                .map(categoryMapper::toCategorySimpleResponse)
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
     * Get root categories
     */
    @GetMapping("/root")
    public ResponseEntity<ApiResponse<List<CategorySimpleResponse>>> getRootCategories() {
        try {
            List<Category> categories = categoryService.getRootCategories();
            List<CategorySimpleResponse> responses = categories.stream()
                .map(categoryMapper::toCategorySimpleResponse)
                .toList();
            return RequestUtil.success("Root categories retrieved successfully", responses);
        } catch (Exception e) {
            log.error("Unexpected error during root categories retrieval", e);
            return RequestUtil.internalError("Failed to retrieve root categories. Please try again.");
        }
    }
    
    /**
     * Get child categories by parent ID
     */
    @GetMapping("/{parentId}/children")
    public ResponseEntity<ApiResponse<List<CategorySimpleResponse>>> getChildCategories(@PathVariable UUID parentId) {
        try {
            List<Category> categories = categoryService.getChildCategories(parentId);
            List<CategorySimpleResponse> responses = categories.stream()
                .map(categoryMapper::toCategorySimpleResponse)
                .toList();
            return RequestUtil.success("Child categories retrieved successfully", responses);
        } catch (Exception e) {
            log.error("Unexpected error during child categories retrieval", e);
            return RequestUtil.internalError("Failed to retrieve child categories. Please try again.");
        }
    }
    
    /**
     * Get all categories in tree structure (with children)
     */
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<CategoryTreeResponse>>> getAllCategoriesTree() {
        try {
            List<Category> rootCategories = categoryService.getRootCategories();
            List<CategoryTreeResponse> treeCategories = categoryMapper.toCategoryTreeResponseList(rootCategories);
            return RequestUtil.success("Categories retrieved successfully", treeCategories);
        } catch (Exception e) {
            log.error("Unexpected error during hierarchical categories retrieval", e);
            return RequestUtil.internalError("Failed to retrieve categories. Please try again.");
        }
    }
    
    /**
     * Get category hierarchy by category ID (with parent and children)
     */
    @GetMapping("/{categoryId}/hierarchy")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryHierarchy(@PathVariable UUID categoryId) {
        try {
            Category category = categoryService.getCategoryHierarchy(categoryId);
            CategoryResponse response = categoryMapper.toCategoryResponse(category);
            return RequestUtil.success("Category hierarchy retrieved successfully", response);
        } catch (NotFoundException e) {
            log.error("Category not found: {}", categoryId);
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during category hierarchy retrieval", e);
            return RequestUtil.internalError("Failed to retrieve category hierarchy. Please try again.");
        }
    }
    
    /**
     * Update category parent
     */
    @PutMapping("/{categoryId}/parent")
    public ResponseEntity<ApiResponse<CategorySimpleResponse>> updateCategoryParent(
            @PathVariable UUID categoryId,
            @RequestParam(required = false) UUID parentId) {
        try {
            Category category = categoryService.updateCategoryParent(categoryId, parentId);
            CategorySimpleResponse response = categoryMapper.toCategorySimpleResponse(category);
            return RequestUtil.success("Category parent updated successfully", response);
        } catch (NotFoundException e) {
            log.error("Category or parent not found: {}", categoryId);
            return RequestUtil.notFound(e.getMessage());
        } catch (ConflictException e) {
            log.error("Category parent update failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Unexpected error during category parent update", e);
            return RequestUtil.internalError("Failed to update category parent. Please try again.");
        }
    }
    
    /**
     * Update category status only
     */
    @PutMapping("/{categoryId}/status")
    public ResponseEntity<ApiResponse<CategorySimpleResponse>> updateCategoryStatus(
            @PathVariable UUID categoryId,
            @RequestParam("value") CategoryStatus status) {
        try {
            Category category = categoryService.updateCategoryStatus(categoryId, status);
            CategorySimpleResponse response = categoryMapper.toCategorySimpleResponse(category);
            return RequestUtil.success("Category status updated successfully", response);
        } catch (NotFoundException e) {
            log.error("Category not found: {}", categoryId);
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during category status update", e);
            return RequestUtil.internalError("Failed to update category status. Please try again.");
        }
    }
    
    /**
     * Get all categories as options for UI select dropdowns
     */
    @GetMapping("/options")
    public ResponseEntity<ApiResponse<List<CategoryOptionResponse>>> getCategoryOptions() {
        try {
            List<Category> categories = categoryService.getAllCategoriesHierarchical();
            List<CategoryOptionResponse> options = categories.stream()
                .map(categoryMapper::toCategoryOptionResponse)
                .toList();
            return RequestUtil.success("Category options retrieved successfully", options);
        } catch (Exception e) {
            log.error("Unexpected error during category options retrieval", e);
            return RequestUtil.internalError("Failed to retrieve category options. Please try again.");
        }
    }
    
    /**
     * Get parent options for a specific category (excludes self and descendants)
     */
    @GetMapping("/{categoryId}/parent-options")
    public ResponseEntity<ApiResponse<List<CategoryOptionResponse>>> getParentOptions(@PathVariable UUID categoryId) {
        try {
            List<Category> categories = categoryService.getParentOptions(categoryId);
            List<CategoryOptionResponse> options = categories.stream()
                .map(categoryMapper::toCategoryOptionResponse)
                .toList();
            return RequestUtil.success("Parent options retrieved successfully", options);
        } catch (NotFoundException e) {
            log.error("Category not found: {}", categoryId);
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during parent options retrieval", e);
            return RequestUtil.internalError("Failed to retrieve parent options. Please try again.");
        }
    }
    
    /**
     * Get maximum level of categories in the database
     */
    @GetMapping("/max-level")
    public ResponseEntity<ApiResponse<Integer>> getMaxLevel() {
        try {
            Integer maxLevel = categoryService.getMaxLevel();
            return RequestUtil.success("Maximum level retrieved successfully", maxLevel);
        } catch (Exception e) {
            log.error("Unexpected error during max level retrieval", e);
            return RequestUtil.internalError("Failed to retrieve maximum level. Please try again.");
        }
    }
    
    /**
     * Delete category by ID
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return RequestUtil.success("Category deleted successfully", null);
        } catch (NotFoundException e) {
            log.error("Category not found: {}", categoryId);
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during category deletion", e);
            return RequestUtil.internalError("Failed to delete category. Please try again.");
        }
    }
}
