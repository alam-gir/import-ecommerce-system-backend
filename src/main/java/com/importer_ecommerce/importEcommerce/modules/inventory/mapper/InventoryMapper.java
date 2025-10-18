package com.importer_ecommerce.importEcommerce.modules.inventory.mapper;

import com.importer_ecommerce.importEcommerce.modules.inventory.dto.response.*;
import com.importer_ecommerce.importEcommerce.modules.inventory.entity.InventoryAlertConfig;
import com.importer_ecommerce.importEcommerce.modules.inventory.entity.InventoryMovement;
import com.importer_ecommerce.importEcommerce.modules.product.entity.ProductVariant;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting inventory entities to response DTOs
 * Handles all inventory-related entity to DTO conversions
 */
@Component
public class InventoryMapper {
    
    /**
     * Convert InventoryMovement entity to StockMovementResponse
     */
    public StockMovementResponse toStockMovementResponse(InventoryMovement movement) {
        if (movement == null) {
            return null;
        }
        
        return new StockMovementResponse(
            movement.getId(),
            movement.getProduct().getId(),
            movement.getProduct().getTitle(),
            movement.getVariant().getId(),
            movement.getVariant().getSku(),
            movement.getMovementType().name(),
            movement.getQuantity(),
            movement.getPreviousStock(),
            movement.getNewStock(),
            movement.getReason(),
            movement.getReferenceId(),
            movement.getCreatedAt(),
            movement.getCreatedBy() != null ? movement.getCreatedBy().toString() : "system"
        );
    }
    
    /**
     * Convert ProductVariant to LowStockAlertResponse
     */
    public LowStockAlertResponse toLowStockAlertResponse(ProductVariant variant, LocalDateTime lastSold) {
        if (variant == null) {
            return null;
        }
        
        int daysUntilOutOfStock = calculateDaysUntilOutOfStock(variant.getStockQuantity(), lastSold);
        
        return new LowStockAlertResponse(
            variant.getProduct().getId(),
            variant.getProduct().getTitle(),
            variant.getId(),
            variant.getSku(),
            variant.getStockQuantity(),
            variant.getLowStockThreshold(),
            lastSold,
            daysUntilOutOfStock
        );
    }
    
    /**
     * Convert ProductVariant to OutOfStockResponse
     */
    public OutOfStockResponse toOutOfStockResponse(ProductVariant variant, LocalDateTime lastSold, Double averageDailySales) {
        if (variant == null) {
            return null;
        }
        
        int daysOutOfStock = calculateDaysOutOfStock(lastSold);
        
        return new OutOfStockResponse(
            variant.getProduct().getId(),
            variant.getProduct().getTitle(),
            variant.getId(),
            variant.getSku(),
            lastSold,
            daysOutOfStock,
            BigDecimal.valueOf(averageDailySales != null ? averageDailySales : 0.0)
        );
    }
    
    /**
     * Convert InventoryAlertConfig entity to InventoryAlertConfigResponse
     */
    public InventoryAlertConfigResponse toInventoryAlertConfigResponse(InventoryAlertConfig config) {
        if (config == null) {
            return null;
        }
        
        return new InventoryAlertConfigResponse(
            config.getGlobalLowStockThreshold(),
            config.getEnableEmailAlerts(),
            config.getEnableDashboardAlerts(),
            config.getAlertFrequency().name(),
            config.getAlertRecipients()
        );
    }
    
    /**
     * Convert list of InventoryMovement entities to StockMovementResponse list
     */
    public List<StockMovementResponse> toStockMovementResponses(List<InventoryMovement> movements) {
        if (movements == null) {
            return List.of();
        }
        
        return movements.stream()
            .map(this::toStockMovementResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Calculate days until out of stock based on current stock and last sale
     */
    private int calculateDaysUntilOutOfStock(Integer currentStock, LocalDateTime lastSold) {
        if (currentStock == null || currentStock <= 0 || lastSold == null) {
            return 0;
        }
        
        // Simple calculation: assume 1 item sold per day
        // In real implementation, this would use historical sales data
        return currentStock;
    }
    
    /**
     * Calculate days out of stock
     */
    private int calculateDaysOutOfStock(LocalDateTime lastSold) {
        if (lastSold == null) {
            return 0;
        }
        
        return (int) ChronoUnit.DAYS.between(lastSold, LocalDateTime.now());
    }
}
