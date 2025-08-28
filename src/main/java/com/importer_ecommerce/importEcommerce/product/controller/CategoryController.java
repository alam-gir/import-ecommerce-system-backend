package com.importer_ecommerce.importEcommerce.product.controller;

import com.importer_ecommerce.importEcommerce.common.utils.ApiResponse;
import com.importer_ecommerce.importEcommerce.product.dto.request.CreateCategoryRequest;
import com.importer_ecommerce.importEcommerce.product.dto.request.UpdateCategoryRequest;
import com.importer_ecommerce.importEcommerce.product.dto.response.CategoryListResponse;
import com.importer_ecommerce.importEcommerce.product.dto.response.CategoryResponse;
import com.importer_ecommerce.importEcommerce.product.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @ModelAttribute CreateCategoryRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Creating category with title: {}", request.getTitle());
        
        CategoryResponse response = categoryService.createCategory(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Category created successfully"));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable UUID id) {
        log.info("Fetching category with ID: {}", id);
        
        CategoryResponse response = categoryService.getCategoryById(id);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Category retrieved successfully"));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<CategoryListResponse>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Fetching all categories with page: {} and size: {}", page, size);
        
        CategoryListResponse response = categoryService.getAllCategories(page, size);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Categories retrieved successfully"));
    }
    
    @GetMapping("/root")
    public ResponseEntity<ApiResponse<CategoryListResponse>> getRootCategories() {
        log.info("Fetching root categories");
        
        CategoryListResponse response = categoryService.getRootCategories();
        
        return ResponseEntity.ok(ApiResponse.success(response, "Root categories retrieved successfully"));
    }
    
    @GetMapping("/{parentId}/children")
    public ResponseEntity<ApiResponse<CategoryListResponse>> getChildrenCategories(
            @PathVariable UUID parentId) {
        
        log.info("Fetching children categories for parent ID: {}", parentId);
        
        CategoryListResponse response = categoryService.getChildrenCategories(parentId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Children categories retrieved successfully"));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<CategoryListResponse>> searchCategories(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Searching categories with query: '{}', page: {} and size: {}", q, page, size);
        
        CategoryListResponse response = categoryService.searchCategories(q, page, size);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Categories search completed"));
    }
    
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable UUID id,
            @Valid @ModelAttribute UpdateCategoryRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Updating category with ID: {}", id);
        
        CategoryResponse response = categoryService.updateCategory(id, request);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Category updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        
        log.info("Deleting category with ID: {}", id);
        
        categoryService.deleteCategory(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Category deleted successfully"));
    }
}
