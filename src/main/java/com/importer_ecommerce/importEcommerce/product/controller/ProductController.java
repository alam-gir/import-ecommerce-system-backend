package com.importer_ecommerce.importEcommerce.product.controller;

import com.importer_ecommerce.importEcommerce.common.utils.ApiResponse;
import com.importer_ecommerce.importEcommerce.product.dto.request.CreateProductRequest;
import com.importer_ecommerce.importEcommerce.product.dto.request.UpdateProductRequest;
import com.importer_ecommerce.importEcommerce.product.dto.response.ProductListResponse;
import com.importer_ecommerce.importEcommerce.product.dto.response.ProductResponse;
import static com.importer_ecommerce.importEcommerce.product.entity.Product.ProductStatus;
import com.importer_ecommerce.importEcommerce.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @ModelAttribute CreateProductRequest request,
            @RequestParam(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles) {
        
        ProductResponse product = productService.createProduct(request, mediaFiles);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(product, "Product created successfully"));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID id,
            @Valid @ModelAttribute UpdateProductRequest request,
            @RequestParam(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles) {
        
        ProductResponse product = productService.updateProduct(id, request, mediaFiles);
        return ResponseEntity.ok(ApiResponse.success(product, "Product updated successfully"));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable UUID id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product, "Product retrieved successfully"));
    }
    
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySlug(@PathVariable String slug) {
        ProductResponse product = productService.getProductBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(product, "Product retrieved successfully"));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<ProductListResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        ProductListResponse products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(ApiResponse.success(products, "Products retrieved successfully"));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<ProductListResponse>> getProductsByStatus(
            @PathVariable ProductStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        ProductListResponse products = productService.getProductsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(products, "Products retrieved successfully"));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<ProductListResponse>> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        ProductListResponse products = productService.searchProducts(query, pageable);
        return ResponseEntity.ok(ApiResponse.success(products, "Products retrieved successfully"));
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<ProductListResponse>> getProductsByCategory(
            @PathVariable UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        ProductListResponse products = productService.getProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success(products, "Products retrieved successfully"));
    }
    
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getFeaturedProducts(
            @RequestParam(defaultValue = "PUBLISHED") ProductStatus status) {
        
        List<ProductResponse> products = productService.getFeaturedProducts(status);
        return ResponseEntity.ok(ApiResponse.success(products, "Featured products retrieved successfully"));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateProductStatus(
            @PathVariable UUID id,
            @RequestParam ProductStatus status) {
        
        productService.updateProductStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(null, "Product status updated successfully"));
    }
    
    // Media management endpoints
    @PostMapping("/{id}/media")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> addProductMedia(
            @PathVariable UUID id,
            @RequestParam("mediaFiles") List<MultipartFile> mediaFiles) {
        
        ProductResponse product = productService.addProductMedia(id, mediaFiles);
        return ResponseEntity.ok(ApiResponse.success(product, "Product media added successfully"));
    }
    
    @DeleteMapping("/{id}/media")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> deleteProductMedia(
            @PathVariable UUID id,
            @RequestBody List<String> mediaUrls) {
        
        ProductResponse product = productService.deleteProductMedia(id, mediaUrls);
        return ResponseEntity.ok(ApiResponse.success(product, "Product media deleted successfully"));
    }
}
