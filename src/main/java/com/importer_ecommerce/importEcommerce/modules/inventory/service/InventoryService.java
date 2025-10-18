package com.importer_ecommerce.importEcommerce.modules.inventory.service;

import com.importer_ecommerce.importEcommerce.modules.inventory.dto.request.BulkStockUpdateRequest;
import com.importer_ecommerce.importEcommerce.modules.inventory.dto.request.StockAdjustmentRequest;
import com.importer_ecommerce.importEcommerce.modules.inventory.dto.request.UpdateAlertConfigRequest;
import com.importer_ecommerce.importEcommerce.modules.inventory.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service interface for Inventory management
 * Handles all inventory-related business operations
 */
public interface InventoryService {
    
    /**
     * Get inventory overview statistics for dashboard
     */
    InventoryOverviewResponse getInventoryOverview();
    
    /**
     * Get variants with low stock below threshold
     */
    Page<LowStockAlertResponse> getLowStockAlerts(Pageable pageable, Integer threshold);
    
    /**
     * Get variants that are out of stock
     */
    Page<OutOfStockResponse> getOutOfStockItems(Pageable pageable);
    
    /**
     * Get stock movement history with filters
     */
    Page<StockMovementResponse> getStockMovementHistory(Pageable pageable, UUID productId, UUID variantId, 
                                                       LocalDateTime from, LocalDateTime to);
    
    /**
     * Perform bulk stock update for multiple variants
     */
    BulkStockUpdateResponse performBulkStockUpdate(BulkStockUpdateRequest request);
    
    /**
     * Perform single stock adjustment
     */
    StockAdjustmentResponse performStockAdjustment(StockAdjustmentRequest request);
    
    /**
     * Generate inventory summary report for date range
     */
    InventoryReportResponse generateInventoryReport(LocalDateTime from, LocalDateTime to);
    
    /**
     * Get current alert configuration
     */
    InventoryAlertConfigResponse getAlertConfig();
    
    /**
     * Update alert configuration
     */
    InventoryAlertConfigResponse updateAlertConfig(UpdateAlertConfigRequest request);
    
    /**
     * Record stock movement (internal method)
     */
    void recordStockMovement(UUID productId, UUID variantId, String movementType, 
                           Integer quantity, Integer previousStock, Integer newStock, 
                           String reason, String referenceId, UUID createdBy);
}
