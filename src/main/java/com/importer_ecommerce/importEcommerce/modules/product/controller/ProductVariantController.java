package com.importer_ecommerce.importEcommerce.modules.product.controller;

import com.importer_ecommerce.importEcommerce.common.dto.response.ApiResponse;
import com.importer_ecommerce.importEcommerce.common.dto.response.PaginationResponse;
import com.importer_ecommerce.importEcommerce.common.exception.ApiException;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.common.util.RequestUtil;
import com.importer_ecommerce.importEcommerce.modules.product.dto.request.CreateProductVariantRequest;
import com.importer_ecommerce.importEcommerce.modules.product.dto.request.LinkVariantAttributeValuesRequest;
import com.importer_ecommerce.importEcommerce.modules.product.dto.request.UpdateProductVariantRequest;
import com.importer_ecommerce.importEcommerce.modules.product.dto.request.UpdateVariantStockRequest;
import com.importer_ecommerce.importEcommerce.modules.product.dto.response.ProductVariantResponse;
import com.importer_ecommerce.importEcommerce.modules.product.entity.ProductVariant;
import com.importer_ecommerce.importEcommerce.modules.product.mapper.ProductVariantMapper;
import com.importer_ecommerce.importEcommerce.modules.product.service.ProductVariantService;
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
import java.util.stream.Collectors;

/**
 * Admin controller for ProductVariant management
 * Handles all product variant-related operations
 */
@RestController
@RequestMapping("/v1/products/{productId}/variants")
@RequiredArgsConstructor
@Slf4j
public class ProductVariantController {

    private final ProductVariantService productVariantService;
    private final ProductVariantMapper productVariantMapper;

    /**
     * Create a new product variant
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Boolean>> createProductVariant(
            @PathVariable UUID productId,
            @Valid @RequestBody CreateProductVariantRequest request) {
        try {
            List<UUID> attributeValueIds = request.getAttributeValueIds() != null 
                ? request.getAttributeValueIds().stream().map(UUID::fromString).collect(Collectors.toList())
                : null;

            boolean success = productVariantService.createProductVariant(
                productId,
                request.getSku(),
                request.getPrice(),
                request.getCompareAtPrice(),
                request.getCostPrice(),
                request.getStockQuantity(),
                request.getLowStockThreshold(),
                request.getWeight(),
                request.getDimensions(),
                request.getBarcode(),
                request.getIsActive(),
                request.getIsTracked(),
                attributeValueIds
            );
            return RequestUtil.success("Product variant created successfully", success);
        } catch (ApiException e) {
            log.error("Product variant creation failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Unexpected error during product variant creation", e);
            return RequestUtil.internalError("Failed to create product variant. Please try again.");
        }
    }

    /**
     * Update an existing product variant
     */
    @PutMapping("/{variantId}")
    public ResponseEntity<ApiResponse<Boolean>> updateProductVariant(
            @PathVariable UUID variantId,
            @Valid @RequestBody UpdateProductVariantRequest request) {
        try {
            List<UUID> attributeValueIds = request.getAttributeValueIds() != null 
                ? request.getAttributeValueIds().stream().map(UUID::fromString).collect(Collectors.toList())
                : null;

            boolean success = productVariantService.updateProductVariant(
                variantId,
                request.getSku(),
                request.getPrice(),
                request.getCompareAtPrice(),
                request.getCostPrice(),
                request.getStockQuantity(),
                request.getLowStockThreshold(),
                request.getWeight(),
                request.getDimensions(),
                request.getBarcode(),
                request.getIsActive(),
                request.getIsTracked(),
                attributeValueIds
            );
            return RequestUtil.success("Product variant updated successfully", success);
        } catch (NotFoundException e) {
            log.error("Product variant not found: {}", variantId);
            return RequestUtil.notFound(e.getMessage());
        } catch (ApiException e) {
            log.error("Product variant update failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Unexpected error during product variant update", e);
            return RequestUtil.internalError("Failed to update product variant. Please try again.");
        }
    }

    /**
     * Get product variant by ID
     */
    @GetMapping("/{variantId}")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> getProductVariantById(@PathVariable UUID variantId) {
        try {
            ProductVariant variant = productVariantService.getProductVariantById(variantId);
            ProductVariantResponse response = productVariantMapper.toProductVariantResponse(variant);
            return RequestUtil.success("Product variant retrieved successfully", response);
        } catch (NotFoundException e) {
            log.error("Product variant not found: {}", variantId);
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during product variant retrieval", e);
            return RequestUtil.internalError("Failed to retrieve product variant. Please try again.");
        }
    }

    /**
     * Get all variants for a product
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductVariantResponse>>> getProductVariantsByProductId(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sku") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<ProductVariant> variantPage = productVariantService.getProductVariantsByProductId(productId, pageable);

            List<ProductVariantResponse> variants = variantPage.getContent()
                .stream()
                .map(productVariantMapper::toProductVariantResponse)
                .toList();

            PaginationResponse pagination = new PaginationResponse(
                variantPage.getNumber(),
                variantPage.getSize(),
                variantPage.getTotalElements(),
                variantPage.getTotalPages(),
                variantPage.hasNext(),
                variantPage.hasPrevious()
            );

            return RequestUtil.success("Product variants retrieved successfully", variants, pagination);
        } catch (Exception e) {
            log.error("Unexpected error during product variants retrieval", e);
            return RequestUtil.internalError("Failed to retrieve product variants. Please try again.");
        }
    }

    /**
     * Delete product variant by ID
     */
    @DeleteMapping("/{variantId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteProductVariant(@PathVariable UUID variantId) {
        try {
            boolean success = productVariantService.deleteProductVariant(variantId);
            return RequestUtil.success("Product variant deleted successfully", success);
        } catch (NotFoundException e) {
            log.error("Product variant not found: {}", variantId);
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during product variant deletion", e);
            return RequestUtil.internalError("Failed to delete product variant. Please try again.");
        }
    }

    /**
     * Update variant stock only
     */
    @PutMapping("/{variantId}/stock")
    public ResponseEntity<ApiResponse<Boolean>> updateVariantStock(
            @PathVariable UUID variantId,
            @Valid @RequestBody UpdateVariantStockRequest request) {
        try {
            boolean success = productVariantService.updateVariantStock(
                variantId,
                request.getStockQuantity(),
                request.getLowStockThreshold()
            );
            return RequestUtil.success("Variant stock updated successfully", success);
        } catch (NotFoundException e) {
            log.error("Product variant not found: {}", variantId);
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during variant stock update", e);
            return RequestUtil.internalError("Failed to update variant stock. Please try again.");
        }
    }

    /**
     * Link attribute values to a variant
     */
    @PutMapping("/{variantId}/attributes")
    public ResponseEntity<ApiResponse<Boolean>> linkAttributeValuesToVariant(
            @PathVariable UUID variantId,
            @Valid @RequestBody LinkVariantAttributeValuesRequest request) {
        try {
            List<UUID> attributeValueIds = request.getAttributeValueIds().stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());

            boolean success = productVariantService.linkAttributeValuesToVariant(variantId, attributeValueIds);
            return RequestUtil.success("Attribute values linked to variant successfully", success);
        } catch (NotFoundException e) {
            log.error("Product variant not found: {}", variantId);
            return RequestUtil.notFound(e.getMessage());
        } catch (ApiException e) {
            log.error("Attribute value linking failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Unexpected error during attribute value linking", e);
            return RequestUtil.internalError("Failed to link attribute values. Please try again.");
        }
    }

    /**
     * Get variants with low stock
     */
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<ProductVariantResponse>>> getVariantsWithLowStock() {
        try {
            List<ProductVariant> variants = productVariantService.getVariantsWithLowStock();
            List<ProductVariantResponse> responses = variants.stream()
                .map(productVariantMapper::toProductVariantResponse)
                .toList();
            return RequestUtil.success("Variants with low stock retrieved successfully", responses);
        } catch (Exception e) {
            log.error("Unexpected error during low stock variants retrieval", e);
            return RequestUtil.internalError("Failed to retrieve variants with low stock. Please try again.");
        }
    }

    /**
     * Get variants out of stock
     */
    @GetMapping("/out-of-stock")
    public ResponseEntity<ApiResponse<List<ProductVariantResponse>>> getVariantsOutOfStock() {
        try {
            List<ProductVariant> variants = productVariantService.getVariantsOutOfStock();
            List<ProductVariantResponse> responses = variants.stream()
                .map(productVariantMapper::toProductVariantResponse)
                .toList();
            return RequestUtil.success("Variants out of stock retrieved successfully", responses);
        } catch (Exception e) {
            log.error("Unexpected error during out of stock variants retrieval", e);
            return RequestUtil.internalError("Failed to retrieve variants out of stock. Please try again.");
        }
    }

    /**
     * Get variants in stock
     */
    @GetMapping("/in-stock")
    public ResponseEntity<ApiResponse<List<ProductVariantResponse>>> getVariantsInStock() {
        try {
            List<ProductVariant> variants = productVariantService.getVariantsInStock();
            List<ProductVariantResponse> responses = variants.stream()
                .map(productVariantMapper::toProductVariantResponse)
                .toList();
            return RequestUtil.success("Variants in stock retrieved successfully", responses);
        } catch (Exception e) {
            log.error("Unexpected error during in stock variants retrieval", e);
            return RequestUtil.internalError("Failed to retrieve variants in stock. Please try again.");
        }
    }

    /**
     * Get variants with discount
     */
    @GetMapping("/with-discount")
    public ResponseEntity<ApiResponse<List<ProductVariantResponse>>> getVariantsWithDiscount() {
        try {
            List<ProductVariant> variants = productVariantService.getVariantsWithDiscount();
            List<ProductVariantResponse> responses = variants.stream()
                .map(productVariantMapper::toProductVariantResponse)
                .toList();
            return RequestUtil.success("Variants with discount retrieved successfully", responses);
        } catch (Exception e) {
            log.error("Unexpected error during discount variants retrieval", e);
            return RequestUtil.internalError("Failed to retrieve variants with discount. Please try again.");
        }
    }
}

