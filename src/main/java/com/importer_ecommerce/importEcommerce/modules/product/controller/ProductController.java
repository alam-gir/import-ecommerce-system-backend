package com.importer_ecommerce.importEcommerce.modules.product.controller;

import com.importer_ecommerce.importEcommerce.common.dto.response.ApiResponse;
import com.importer_ecommerce.importEcommerce.common.dto.response.PaginationResponse;
import com.importer_ecommerce.importEcommerce.common.exception.ApiException;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.common.util.RequestUtil;
import com.importer_ecommerce.importEcommerce.modules.product.dto.request.CreateProductRequest;
import com.importer_ecommerce.importEcommerce.modules.product.dto.request.UpdateProductRequest;
import com.importer_ecommerce.importEcommerce.modules.product.dto.response.ProductResponse;
import com.importer_ecommerce.importEcommerce.modules.product.dto.response.ProductSummaryResponse;
import com.importer_ecommerce.importEcommerce.modules.product.entity.Product;
import com.importer_ecommerce.importEcommerce.modules.product.mapper.ProductMapper;
import com.importer_ecommerce.importEcommerce.modules.product.service.ProductService;
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
 * Admin controller for Product management
 * Handles all product-related operations
 */
@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    /**
     * Create a new product
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Boolean>> createProduct(@Valid @ModelAttribute CreateProductRequest request) {
        try {
            boolean success = productService.createProduct(
                request.getTitle(),
                request.getDescription(),
                request.getCategoryId(),
                request.getMinimumOrderQuantity(),
                request.getProfileImage(),
                request.getImages(),
                request.getDescriptionImages()
            );
            return RequestUtil.success("Product created successfully", success);
        } catch (ApiException e) {
            log.error("Product creation failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Unexpected error during product creation", e);
            return RequestUtil.internalError("Failed to create product. Please try again.");
        }
    }

    /**
     * Update an existing product
     */
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> updateProduct(
            @PathVariable UUID productId,
            @Valid @ModelAttribute UpdateProductRequest request) {
        try {
            boolean success = productService.updateProduct(
                productId,
                request.getTitle(),
                request.getDescription(),
                request.getCategoryId(),
                request.getMinimumOrderQuantity(),
                request.getProfileImage(),
                request.getImages(),
                request.getDescriptionImages(),
                request.getExistingImages(),
                request.getExistingDescriptionImages()
            );
            return RequestUtil.success("Product updated successfully", success);
        } catch (NotFoundException e) {
            log.error("Product not found: {}", productId);
            return RequestUtil.notFound(e.getMessage());
        } catch (ApiException e) {
            log.error("Product update failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Unexpected error during product update", e);
            return RequestUtil.internalError("Failed to update product. Please try again.");
        }
    }

    /**
     * Get product by ID with full details
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable UUID productId) {
        try {
            Product product = productService.getProductById(productId);
            ProductResponse response = productMapper.toProductResponse(product);
            return RequestUtil.success("Product retrieved successfully", response);
        } catch (NotFoundException e) {
            log.error("Product not found: {}", productId);
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during product retrieval", e);
            return RequestUtil.internalError("Failed to retrieve product. Please try again.");
        }
    }

    /**
     * Get product summary by ID (without full details)
     */
    @GetMapping("/{productId}/summary")
    public ResponseEntity<ApiResponse<ProductSummaryResponse>> getProductSummaryById(@PathVariable UUID productId) {
        try {
            Product product = productService.getProductBasicById(productId);
            ProductSummaryResponse response = productMapper.toProductSummaryResponse(product);
            return RequestUtil.success("Product summary retrieved successfully", response);
        } catch (NotFoundException e) {
            log.error("Product not found: {}", productId);
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during product summary retrieval", e);
            return RequestUtil.internalError("Failed to retrieve product summary. Please try again.");
        }
    }

    /**
     * Get all products with pagination and filtering
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductSummaryResponse>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String categoryId) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Product> productPage = productService.getAllProducts(pageable, search, categoryId);

            List<ProductSummaryResponse> products = productPage.getContent()
                .stream()
                .map(productMapper::toProductSummaryResponse)
                .toList();

            PaginationResponse pagination = new PaginationResponse(
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.hasNext(),
                productPage.hasPrevious()
            );

            return RequestUtil.success("Products retrieved successfully", products, pagination);
        } catch (Exception e) {
            log.error("Unexpected error during products retrieval", e);
            return RequestUtil.internalError("Failed to retrieve products. Please try again.");
        }
    }

    /**
     * Get products by category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductSummaryResponse>>> getProductsByCategory(@PathVariable UUID categoryId) {
        try {
            List<Product> products = productService.getProductsByCategory(categoryId);
            List<ProductSummaryResponse> responses = products.stream()
                .map(productMapper::toProductSummaryResponse)
                .toList();
            return RequestUtil.success("Products retrieved successfully", responses);
        } catch (Exception e) {
            log.error("Unexpected error during products retrieval by category", e);
            return RequestUtil.internalError("Failed to retrieve products. Please try again.");
        }
    }

    /**
     * Delete product by ID
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteProduct(@PathVariable UUID productId) {
        try {
            boolean success = productService.deleteProduct(productId);
            return RequestUtil.success("Product deleted successfully", success);
        } catch (NotFoundException e) {
            log.error("Product not found: {}", productId);
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during product deletion", e);
            return RequestUtil.internalError("Failed to delete product. Please try again.");
        }
    }

    /**
     * Get products with low stock
     */
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<ProductSummaryResponse>>> getProductsWithLowStock() {
        try {
            List<Product> products = productService.getProductsWithLowStock();
            List<ProductSummaryResponse> responses = products.stream()
                .map(productMapper::toProductSummaryResponse)
                .toList();
            return RequestUtil.success("Products with low stock retrieved successfully", responses);
        } catch (Exception e) {
            log.error("Unexpected error during low stock products retrieval", e);
            return RequestUtil.internalError("Failed to retrieve products with low stock. Please try again.");
        }
    }

    /**
     * Get products without variants
     */
    @GetMapping("/without-variants")
    public ResponseEntity<ApiResponse<List<ProductSummaryResponse>>> getProductsWithoutVariants() {
        try {
            List<Product> products = productService.getProductsWithoutVariants();
            List<ProductSummaryResponse> responses = products.stream()
                .map(productMapper::toProductSummaryResponse)
                .toList();
            return RequestUtil.success("Products without variants retrieved successfully", responses);
        } catch (Exception e) {
            log.error("Unexpected error during products without variants retrieval", e);
            return RequestUtil.internalError("Failed to retrieve products without variants. Please try again.");
        }
    }

    /**
     * Get products with variants
     */
    @GetMapping("/with-variants")
    public ResponseEntity<ApiResponse<List<ProductSummaryResponse>>> getProductsWithVariants() {
        try {
            List<Product> products = productService.getProductsWithVariants();
            List<ProductSummaryResponse> responses = products.stream()
                .map(productMapper::toProductSummaryResponse)
                .toList();
            return RequestUtil.success("Products with variants retrieved successfully", responses);
        } catch (Exception e) {
            log.error("Unexpected error during products with variants retrieval", e);
            return RequestUtil.internalError("Failed to retrieve products with variants. Please try again.");
        }
    }
}

