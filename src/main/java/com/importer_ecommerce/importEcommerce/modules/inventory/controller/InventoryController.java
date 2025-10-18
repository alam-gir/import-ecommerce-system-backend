package com.importer_ecommerce.importEcommerce.modules.inventory.controller;

import com.importer_ecommerce.importEcommerce.common.dto.response.ApiResponse;
import com.importer_ecommerce.importEcommerce.common.dto.response.PaginationResponse;
import com.importer_ecommerce.importEcommerce.common.exception.ApiException;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.common.util.RequestUtil;
import com.importer_ecommerce.importEcommerce.modules.inventory.dto.request.BulkStockUpdateRequest;
import com.importer_ecommerce.importEcommerce.modules.inventory.dto.request.StockAdjustmentRequest;
import com.importer_ecommerce.importEcommerce.modules.inventory.dto.request.UpdateAlertConfigRequest;
import com.importer_ecommerce.importEcommerce.modules.inventory.dto.response.*;
import com.importer_ecommerce.importEcommerce.modules.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Admin controller for Inventory management
 * Handles all inventory-related operations for admin dashboard
 */
@RestController
@RequestMapping("/v1/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Get inventory overview statistics for dashboard
     */
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<InventoryOverviewResponse>> getInventoryOverview() {
        try {
            InventoryOverviewResponse overview = inventoryService.getInventoryOverview();
            return RequestUtil.success("Inventory overview retrieved successfully", overview);
        } catch (Exception e) {
            log.error("Unexpected error during inventory overview retrieval", e);
            return RequestUtil.internalError("Failed to retrieve inventory overview. Please try again.");
        }
    }

    /**
     * Get variants with low stock below threshold
     */
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<LowStockAlertResponse>>> getLowStockAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer threshold) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "stockQuantity"));
            Page<LowStockAlertResponse> alerts = inventoryService.getLowStockAlerts(pageable, threshold);
            
            PaginationResponse pagination = PaginationResponse.from(alerts);
            
            return RequestUtil.success("Low stock alerts retrieved successfully", alerts.getContent(), pagination);
        } catch (Exception e) {
            log.error("Unexpected error during low stock alerts retrieval", e);
            return RequestUtil.internalError("Failed to retrieve low stock alerts. Please try again.");
        }
    }

    /**
     * Get variants that are out of stock
     */
    @GetMapping("/out-of-stock")
    public ResponseEntity<ApiResponse<List<OutOfStockResponse>>> getOutOfStockItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sku"));
            Page<OutOfStockResponse> outOfStockItems = inventoryService.getOutOfStockItems(pageable);
            
            PaginationResponse pagination = PaginationResponse.from(outOfStockItems);
            
            return RequestUtil.success("Out of stock items retrieved successfully", outOfStockItems.getContent(), pagination);
        } catch (Exception e) {
            log.error("Unexpected error during out of stock items retrieval", e);
            return RequestUtil.internalError("Failed to retrieve out of stock items. Please try again.");
        }
    }

    /**
     * Get stock movement history with filters
     */
    @GetMapping("/stock-movement")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> getStockMovementHistory(
            @RequestParam(required = false) UUID productId,
            @RequestParam(required = false) UUID variantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            
            // Log the date parameters for debugging
            log.info("Stock movement query - from: {}, to: {}, productId: {}, variantId: {}", from, to, productId, variantId);
            
            Page<StockMovementResponse> movements = inventoryService.getStockMovementHistory(pageable, productId, variantId, from, to);
            
            PaginationResponse pagination = PaginationResponse.from(movements);
            
            return RequestUtil.success("Stock movement history retrieved successfully", movements.getContent(), pagination);
        } catch (Exception e) {
            log.error("Unexpected error during stock movement history retrieval", e);
            return RequestUtil.internalError("Failed to retrieve stock movement history. Please try again.");
        }
    }

    /**
     * Perform bulk stock update for multiple variants
     */
    @PostMapping("/bulk-update")
    public ResponseEntity<ApiResponse<BulkStockUpdateResponse>> performBulkStockUpdate(
            @Valid @RequestBody BulkStockUpdateRequest request) {
        try {
            BulkStockUpdateResponse result = inventoryService.performBulkStockUpdate(request);
            return RequestUtil.success("Bulk stock update completed", result);
        } catch (ApiException e) {
            log.error("Bulk stock update failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Unexpected error during bulk stock update", e);
            return RequestUtil.internalError("Failed to perform bulk stock update. Please try again.");
        }
    }

    /**
     * Perform single stock adjustment
     */
    @PostMapping("/adjustment")
    public ResponseEntity<ApiResponse<StockAdjustmentResponse>> performStockAdjustment(
            @Valid @RequestBody StockAdjustmentRequest request) {
        try {
            StockAdjustmentResponse result = inventoryService.performStockAdjustment(request);
            return RequestUtil.success("Stock adjustment completed successfully", result);
        } catch (NotFoundException e) {
            log.error("Variant not found: {}", request.getVariantId());
            return RequestUtil.notFound(e.getMessage());
        } catch (ApiException e) {
            log.error("Stock adjustment failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Unexpected error during stock adjustment", e);
            return RequestUtil.internalError("Failed to perform stock adjustment. Please try again.");
        }
    }

    /**
     * Generate inventory summary report for date range
     */
    @GetMapping("/reports/stock-summary")
    public ResponseEntity<ApiResponse<InventoryReportResponse>> generateInventoryReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        try {
            InventoryReportResponse report = inventoryService.generateInventoryReport(from, to);
            return RequestUtil.success("Inventory report generated successfully", report);
        } catch (Exception e) {
            log.error("Unexpected error during inventory report generation", e);
            return RequestUtil.internalError("Failed to generate inventory report. Please try again.");
        }
    }

    /**
     * Get current alert configuration
     */
    @GetMapping("/alerts/config")
    public ResponseEntity<ApiResponse<InventoryAlertConfigResponse>> getAlertConfig() {
        try {
            InventoryAlertConfigResponse config = inventoryService.getAlertConfig();
            return RequestUtil.success("Alert configuration retrieved successfully", config);
        } catch (Exception e) {
            log.error("Unexpected error during alert config retrieval", e);
            return RequestUtil.internalError("Failed to retrieve alert configuration. Please try again.");
        }
    }

    /**
     * Update alert configuration
     */
    @PutMapping("/alerts/config")
    public ResponseEntity<ApiResponse<InventoryAlertConfigResponse>> updateAlertConfig(
            @Valid @RequestBody UpdateAlertConfigRequest request) {
        try {
            InventoryAlertConfigResponse config = inventoryService.updateAlertConfig(request);
            return RequestUtil.success("Alert configuration updated successfully", config);
        } catch (ApiException e) {
            log.error("Alert config update failed: {}", e.getMessage());
            return RequestUtil.error(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            log.error("Unexpected error during alert config update", e);
            return RequestUtil.internalError("Failed to update alert configuration. Please try again.");
        }
    }
}
