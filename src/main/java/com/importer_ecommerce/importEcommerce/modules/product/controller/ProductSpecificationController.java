package com.importer_ecommerce.importEcommerce.modules.product.controller;

import com.importer_ecommerce.importEcommerce.common.dto.response.ApiResponse;
import com.importer_ecommerce.importEcommerce.common.exception.ApiException;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.common.util.RequestUtil;
import com.importer_ecommerce.importEcommerce.modules.product.dto.request.CreateProductSpecificationRequest;
import com.importer_ecommerce.importEcommerce.modules.product.dto.request.UpdateProductSpecificationRequest;
import com.importer_ecommerce.importEcommerce.modules.product.dto.response.ProductSpecificationResponse;
import com.importer_ecommerce.importEcommerce.modules.product.entity.ProductSpecification;
import com.importer_ecommerce.importEcommerce.modules.product.mapper.ProductSpecificationMapper;
import com.importer_ecommerce.importEcommerce.modules.product.service.ProductSpecificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Admin controller for ProductSpecification management
 * Handles all product specification-related operations
 */
@RestController
@RequestMapping("/v1/products/{productId}/specifications")
@RequiredArgsConstructor
@Slf4j
public class ProductSpecificationController {

    private final ProductSpecificationService productSpecificationService;
    private final ProductSpecificationMapper productSpecificationMapper;

    /**
     * Create a new product specification
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Boolean>> createProductSpecification(
            @PathVariable UUID productId,
            @Valid @RequestBody CreateProductSpecificationRequest request) {
        try {
            boolean success = productSpecificationService.createProductSpecification(
                productId,
                request.getName(),
                request.getValue()
            );
            return RequestUtil.success("Product specification created successfully", success);
        } catch (ApiException e) {
            log.error("Product specification creation failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Unexpected error during product specification creation", e);
            return RequestUtil.internalError("Failed to create product specification. Please try again.");
        }
    }

    /**
     * Update an existing product specification
     */
    @PutMapping("/{specificationId}")
    public ResponseEntity<ApiResponse<Boolean>> updateProductSpecification(
            @PathVariable UUID specificationId,
            @Valid @RequestBody UpdateProductSpecificationRequest request) {
        try {
            boolean success = productSpecificationService.updateProductSpecification(
                specificationId,
                request.getName(),
                request.getValue()
            );
            return RequestUtil.success("Product specification updated successfully", success);
        } catch (NotFoundException e) {
            log.error("Product specification not found: {}", specificationId);
            return RequestUtil.notFound(e.getMessage());
        } catch (ApiException e) {
            log.error("Product specification update failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Unexpected error during product specification update", e);
            return RequestUtil.internalError("Failed to update product specification. Please try again.");
        }
    }

    /**
     * Get product specification by ID
     */
    @GetMapping("/{specificationId}")
    public ResponseEntity<ApiResponse<ProductSpecificationResponse>> getProductSpecificationById(@PathVariable UUID specificationId) {
        try {
            ProductSpecification specification = productSpecificationService.getProductSpecificationById(specificationId);
            ProductSpecificationResponse response = productSpecificationMapper.toProductSpecificationResponse(specification);
            return RequestUtil.success("Product specification retrieved successfully", response);
        } catch (NotFoundException e) {
            log.error("Product specification not found: {}", specificationId);
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during product specification retrieval", e);
            return RequestUtil.internalError("Failed to retrieve product specification. Please try again.");
        }
    }

    /**
     * Get all specifications for a product
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductSpecificationResponse>>> getProductSpecificationsByProductId(@PathVariable UUID productId) {
        try {
            List<ProductSpecification> specifications = productSpecificationService.getProductSpecificationsByProductId(productId);

            List<ProductSpecificationResponse> responses = specifications.stream()
                .map(productSpecificationMapper::toProductSpecificationResponse)
                .toList();

            return RequestUtil.success("Product specifications retrieved successfully", responses);
        } catch (Exception e) {
            log.error("Unexpected error during product specifications retrieval", e);
            return RequestUtil.internalError("Failed to retrieve product specifications. Please try again.");
        }
    }

    /**
     * Delete product specification by ID
     */
    @DeleteMapping("/{specificationId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteProductSpecification(@PathVariable UUID specificationId) {
        try {
            boolean success = productSpecificationService.deleteProductSpecification(specificationId);
            return RequestUtil.success("Product specification deleted successfully", success);
        } catch (NotFoundException e) {
            log.error("Product specification not found: {}", specificationId);
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during product specification deletion", e);
            return RequestUtil.internalError("Failed to delete product specification. Please try again.");
        }
    }

}

