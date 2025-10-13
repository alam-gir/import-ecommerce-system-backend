package com.importer_ecommerce.importEcommerce.modules.product.controller;

import com.importer_ecommerce.importEcommerce.common.dto.response.ApiResponse;
import com.importer_ecommerce.importEcommerce.common.dto.response.PaginationResponse;
import com.importer_ecommerce.importEcommerce.common.exception.ApiException;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.common.util.RequestUtil;
import com.importer_ecommerce.importEcommerce.modules.product.dto.request.CreateVariantAttributeRequest;
import com.importer_ecommerce.importEcommerce.modules.product.dto.request.CreateVariantAttributeValueRequest;
import com.importer_ecommerce.importEcommerce.modules.product.dto.request.UpdateVariantAttributeRequest;
import com.importer_ecommerce.importEcommerce.modules.product.dto.request.UpdateVariantAttributeValueRequest;
import com.importer_ecommerce.importEcommerce.modules.product.dto.response.VariantAttributeResponse;
import com.importer_ecommerce.importEcommerce.modules.product.dto.response.VariantAttributeValueResponse;
import com.importer_ecommerce.importEcommerce.modules.product.entity.VariantAttribute;
import com.importer_ecommerce.importEcommerce.modules.product.entity.VariantAttributeValue;
import com.importer_ecommerce.importEcommerce.modules.product.mapper.VariantAttributeMapper;
import com.importer_ecommerce.importEcommerce.modules.product.service.VariantAttributeService;
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
 * Admin controller for VariantAttribute management
 * Handles all variant attribute-related operations
 */
@RestController
@RequestMapping("/v1/products/{productId}/attributes")
@RequiredArgsConstructor
@Slf4j
public class VariantAttributeController {

    private final VariantAttributeService variantAttributeService;
    private final VariantAttributeMapper variantAttributeMapper;

    /**
     * Create a new variant attribute
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Boolean>> createVariantAttribute(
            @PathVariable UUID productId,
            @Valid @RequestBody CreateVariantAttributeRequest request) {
        try {
            boolean success = variantAttributeService.createVariantAttribute(
                productId,
                request.getName(),
                request.getAttributeType()
            );
            return RequestUtil.success("Variant attribute created successfully", success);
        } catch (ApiException e) {
            log.error("Variant attribute creation failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Unexpected error during variant attribute creation", e);
            return RequestUtil.internalError("Failed to create variant attribute. Please try again.");
        }
    }

    /**
     * Update an existing variant attribute
     */
    @PutMapping("/{attributeId}")
    public ResponseEntity<ApiResponse<Boolean>> updateVariantAttribute(
            @PathVariable UUID attributeId,
            @Valid @RequestBody UpdateVariantAttributeRequest request) {
        try {
            boolean success = variantAttributeService.updateVariantAttribute(
                attributeId,
                request.getName(),
                request.getAttributeType()
            );
            return RequestUtil.success("Variant attribute updated successfully", success);
        } catch (NotFoundException e) {
            log.error("Variant attribute not found: {}", attributeId);
            return RequestUtil.notFound(e.getMessage());
        } catch (ApiException e) {
            log.error("Variant attribute update failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Unexpected error during variant attribute update", e);
            return RequestUtil.internalError("Failed to update variant attribute. Please try again.");
        }
    }

    /**
     * Get variant attribute by ID
     */
    @GetMapping("/{attributeId}")
    public ResponseEntity<ApiResponse<VariantAttributeResponse>> getVariantAttributeById(@PathVariable UUID attributeId) {
        try {
            VariantAttribute attribute = variantAttributeService.getVariantAttributeById(attributeId);
            VariantAttributeResponse response = variantAttributeMapper.toVariantAttributeResponse(attribute);
            return RequestUtil.success("Variant attribute retrieved successfully", response);
        } catch (NotFoundException e) {
            log.error("Variant attribute not found: {}", attributeId);
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during variant attribute retrieval", e);
            return RequestUtil.internalError("Failed to retrieve variant attribute. Please try again.");
        }
    }

    /**
     * Get all variant attributes for a product
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<VariantAttributeResponse>>> getVariantAttributesByProductId(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<VariantAttribute> attributePage = variantAttributeService.getVariantAttributesByProductId(productId, pageable);

            List<VariantAttributeResponse> attributes = attributePage.getContent()
                .stream()
                .map(variantAttributeMapper::toVariantAttributeResponse)
                .toList();

            PaginationResponse pagination = new PaginationResponse(
                attributePage.getNumber(),
                attributePage.getSize(),
                attributePage.getTotalElements(),
                attributePage.getTotalPages(),
                attributePage.hasNext(),
                attributePage.hasPrevious()
            );

            return RequestUtil.success("Variant attributes retrieved successfully", attributes, pagination);
        } catch (Exception e) {
            log.error("Unexpected error during variant attributes retrieval", e);
            return RequestUtil.internalError("Failed to retrieve variant attributes. Please try again.");
        }
    }

    /**
     * Delete variant attribute by ID
     */
    @DeleteMapping("/{attributeId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteVariantAttribute(@PathVariable UUID attributeId) {
        try {
            boolean success = variantAttributeService.deleteVariantAttribute(attributeId);
            return RequestUtil.success("Variant attribute deleted successfully", success);
        } catch (NotFoundException e) {
            log.error("Variant attribute not found: {}", attributeId);
            return RequestUtil.notFound(e.getMessage());
        } catch (ApiException e) {
            log.error("Variant attribute deletion failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Unexpected error during variant attribute deletion", e);
            return RequestUtil.internalError("Failed to delete variant attribute. Please try again.");
        }
    }

    /**
     * Create a new variant attribute value
     */
    @PostMapping("/{attributeId}/values")
    public ResponseEntity<ApiResponse<Boolean>> createVariantAttributeValue(
            @PathVariable UUID attributeId,
            @Valid @ModelAttribute CreateVariantAttributeValueRequest request) {
        try {
            boolean success = variantAttributeService.createVariantAttributeValue(
                attributeId,
                request.getValue(),
                request.getImage()
            );
            return RequestUtil.success("Variant attribute value created successfully", success);
        } catch (ApiException e) {
            log.error("Variant attribute value creation failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Unexpected error during variant attribute value creation", e);
            return RequestUtil.internalError("Failed to create variant attribute value. Please try again.");
        }
    }

    /**
     * Update an existing variant attribute value
     */
    @PutMapping("/{attributeId}/values/{valueId}")
    public ResponseEntity<ApiResponse<Boolean>> updateVariantAttributeValue(
            @PathVariable UUID valueId,
            @Valid @ModelAttribute UpdateVariantAttributeValueRequest request) {
        try {
            boolean success = variantAttributeService.updateVariantAttributeValue(
                valueId,
                request.getValue(),
                request.getImage()
            );
            return RequestUtil.success("Variant attribute value updated successfully", success);
        } catch (NotFoundException e) {
            log.error("Variant attribute value not found: {}", valueId);
            return RequestUtil.notFound(e.getMessage());
        } catch (ApiException e) {
            log.error("Variant attribute value update failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Unexpected error during variant attribute value update", e);
            return RequestUtil.internalError("Failed to update variant attribute value. Please try again.");
        }
    }

    /**
     * Get variant attribute value by ID
     */
    @GetMapping("/{attributeId}/values/{valueId}")
    public ResponseEntity<ApiResponse<VariantAttributeValueResponse>> getVariantAttributeValueById(@PathVariable UUID valueId) {
        try {
            VariantAttributeValue value = variantAttributeService.getVariantAttributeValueById(valueId);
            VariantAttributeValueResponse response = variantAttributeMapper.toVariantAttributeValueResponse(value);
            return RequestUtil.success("Variant attribute value retrieved successfully", response);
        } catch (NotFoundException e) {
            log.error("Variant attribute value not found: {}", valueId);
            return RequestUtil.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during variant attribute value retrieval", e);
            return RequestUtil.internalError("Failed to retrieve variant attribute value. Please try again.");
        }
    }

    /**
     * Get all values for a variant attribute
     */
    @GetMapping("/{attributeId}/values")
    public ResponseEntity<ApiResponse<List<VariantAttributeValueResponse>>> getVariantAttributeValuesByAttributeId(
            @PathVariable UUID attributeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "value") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<VariantAttributeValue> valuePage = variantAttributeService.getVariantAttributeValuesByAttributeId(attributeId, pageable);

            List<VariantAttributeValueResponse> values = valuePage.getContent()
                .stream()
                .map(variantAttributeMapper::toVariantAttributeValueResponse)
                .toList();

            PaginationResponse pagination = new PaginationResponse(
                valuePage.getNumber(),
                valuePage.getSize(),
                valuePage.getTotalElements(),
                valuePage.getTotalPages(),
                valuePage.hasNext(),
                valuePage.hasPrevious()
            );

            return RequestUtil.success("Variant attribute values retrieved successfully", values, pagination);
        } catch (Exception e) {
            log.error("Unexpected error during variant attribute values retrieval", e);
            return RequestUtil.internalError("Failed to retrieve variant attribute values. Please try again.");
        }
    }

    /**
     * Delete variant attribute value by ID
     */
    @DeleteMapping("/{attributeId}/values/{valueId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteVariantAttributeValue(@PathVariable UUID valueId) {
        try {
            boolean success = variantAttributeService.deleteVariantAttributeValue(valueId);
            return RequestUtil.success("Variant attribute value deleted successfully", success);
        } catch (NotFoundException e) {
            log.error("Variant attribute value not found: {}", valueId);
            return RequestUtil.notFound(e.getMessage());
        } catch (ApiException e) {
            log.error("Variant attribute value deletion failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Unexpected error during variant attribute value deletion", e);
            return RequestUtil.internalError("Failed to delete variant attribute value. Please try again.");
        }
    }
}
