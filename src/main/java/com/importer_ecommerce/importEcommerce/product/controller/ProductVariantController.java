package com.importer_ecommerce.importEcommerce.product.controller;

import com.importer_ecommerce.importEcommerce.common.utils.ApiResponse;
import com.importer_ecommerce.importEcommerce.product.dto.request.CreateProductVariantRequest;
import com.importer_ecommerce.importEcommerce.product.dto.request.DeleteProductMediaRequest;
import com.importer_ecommerce.importEcommerce.product.dto.request.UpdateProductVariantRequest;
import com.importer_ecommerce.importEcommerce.product.dto.response.ProductVariantListResponse;
import com.importer_ecommerce.importEcommerce.product.dto.response.ProductVariantResponse;
import com.importer_ecommerce.importEcommerce.product.service.ProductVariantService;
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
@RequestMapping("/product-variants")
@RequiredArgsConstructor
public class ProductVariantController {
    
    private final ProductVariantService productVariantService;
    
    @PostMapping("/products/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> createProductVariant(
            @PathVariable UUID productId,
            @Valid @ModelAttribute CreateProductVariantRequest request,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        
        ProductVariantResponse variant = productVariantService.createProductVariant(productId, request, images);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(variant, "Product variant created successfully"));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> updateProductVariant(
            @PathVariable UUID id,
            @Valid @ModelAttribute UpdateProductVariantRequest request,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        
        ProductVariantResponse variant = productVariantService.updateProductVariant(id, request, images);
        return ResponseEntity.ok(ApiResponse.success(variant, "Product variant updated successfully"));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> getProductVariantById(@PathVariable UUID id) {
        ProductVariantResponse variant = productVariantService.getProductVariantById(id);
        return ResponseEntity.ok(ApiResponse.success(variant, "Product variant retrieved successfully"));
    }
    
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> getProductVariantBySku(@PathVariable String sku) {
        ProductVariantResponse variant = productVariantService.getProductVariantBySku(sku);
        return ResponseEntity.ok(ApiResponse.success(variant, "Product variant retrieved successfully"));
    }
    
    @GetMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<ProductVariantListResponse>> getProductVariantsByProduct(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        ProductVariantListResponse variants = productVariantService.getProductVariantsByProduct(productId, pageable);
        return ResponseEntity.ok(ApiResponse.success(variants, "Product variants retrieved successfully"));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<ProductVariantListResponse>> getAllProductVariants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        ProductVariantListResponse variants = productVariantService.getAllProductVariants(pageable);
        return ResponseEntity.ok(ApiResponse.success(variants, "Product variants retrieved successfully"));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<ProductVariantListResponse>> searchProductVariants(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        ProductVariantListResponse variants = productVariantService.searchProductVariants(query, pageable);
        return ResponseEntity.ok(ApiResponse.success(variants, "Product variants search completed successfully"));
    }
    
    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductVariantListResponse>> getLowStockVariants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        ProductVariantListResponse variants = productVariantService.getLowStockVariants(pageable);
        return ResponseEntity.ok(ApiResponse.success(variants, "Low stock variants retrieved successfully"));
    }
    
    @GetMapping("/out-of-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductVariantListResponse>> getOutOfStockVariants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        ProductVariantListResponse variants = productVariantService.getOutOfStockVariants(pageable);
        return ResponseEntity.ok(ApiResponse.success(variants, "Out of stock variants retrieved successfully"));
    }
    
    @GetMapping("/products/{productId}/active")
    public ResponseEntity<ApiResponse<List<ProductVariantResponse>>> getActiveVariantsByProduct(@PathVariable UUID productId) {
        List<ProductVariantResponse> variants = productVariantService.getActiveVariantsByProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(variants, "Active variants retrieved successfully"));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProductVariant(@PathVariable UUID id) {
        productVariantService.deleteProductVariant(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product variant deleted successfully"));
    }
    
    @PatchMapping("/{id}/inventory")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateInventoryQuantity(
            @PathVariable UUID id,
            @RequestParam Integer quantity) {
        
        productVariantService.updateInventoryQuantity(id, quantity);
        return ResponseEntity.ok(ApiResponse.success(null, "Inventory quantity updated successfully"));
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateVariantStatus(
            @PathVariable UUID id,
            @RequestParam Boolean isActive) {
        
        productVariantService.updateVariantStatus(id, isActive);
        return ResponseEntity.ok(ApiResponse.success(null, "Variant status updated successfully"));
    }
    
    // Media management endpoints
    @PostMapping("/{id}/media")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> addVariantMedia(
            @PathVariable UUID id,
            @RequestParam("images") List<MultipartFile> images) {
        
        ProductVariantResponse variant = productVariantService.addVariantMedia(id, images);
        return ResponseEntity.ok(ApiResponse.success(variant, "Variant media added successfully"));
    }
    
    @DeleteMapping("/{id}/media")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> deleteVariantMedia(
            @PathVariable UUID id,
            @Valid @RequestBody DeleteProductMediaRequest request) {
        
        ProductVariantResponse variant = productVariantService.deleteVariantMedia(id, request.getMediaUrls());
        return ResponseEntity.ok(ApiResponse.success(variant, "Variant media deleted successfully"));
    }
}
