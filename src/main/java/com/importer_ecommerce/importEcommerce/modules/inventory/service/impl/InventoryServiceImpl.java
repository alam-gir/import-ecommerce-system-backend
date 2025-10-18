package com.importer_ecommerce.importEcommerce.modules.inventory.service.impl;

import com.importer_ecommerce.importEcommerce.common.exception.ApiException;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.modules.inventory.dto.request.BulkStockUpdateRequest;
import com.importer_ecommerce.importEcommerce.modules.inventory.dto.request.StockAdjustmentRequest;
import com.importer_ecommerce.importEcommerce.modules.inventory.dto.request.UpdateAlertConfigRequest;
import com.importer_ecommerce.importEcommerce.modules.inventory.dto.response.*;
import com.importer_ecommerce.importEcommerce.modules.inventory.entity.InventoryAlertConfig;
import com.importer_ecommerce.importEcommerce.modules.inventory.entity.InventoryMovement;
import com.importer_ecommerce.importEcommerce.modules.inventory.mapper.InventoryMapper;
import com.importer_ecommerce.importEcommerce.modules.inventory.repository.InventoryAlertConfigRepository;
import com.importer_ecommerce.importEcommerce.modules.inventory.repository.InventoryMovementRepository;
import com.importer_ecommerce.importEcommerce.modules.inventory.service.InventoryService;
import com.importer_ecommerce.importEcommerce.modules.product.entity.ProductVariant;
import com.importer_ecommerce.importEcommerce.modules.product.repository.ProductRepository;
import com.importer_ecommerce.importEcommerce.modules.product.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of InventoryService
 * Handles all inventory-related business operations with proper error handling
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    
    private final InventoryMovementRepository inventoryMovementRepository;
    private final InventoryAlertConfigRepository alertConfigRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryMapper inventoryMapper;
    
    @Override
    @Transactional(readOnly = true)
    public InventoryOverviewResponse getInventoryOverview() {
        try {
            // Get total products count
            long totalProducts = productRepository.count();
            
            // Get total variants count
            long totalVariants = productVariantRepository.count();
            
            // Calculate total stock value (simplified - would need price calculation)
            BigDecimal totalStockValue = calculateTotalStockValue();
            
            // Get low stock items count
            int lowStockItems = getLowStockItemsCount();
            
            // Get out of stock items count
            int outOfStockItems = getOutOfStockItemsCount();
            
            // Get overstock items count (simplified)
            int overstockItems = getOverstockItemsCount();
            
            // Get total stock quantity
            int totalStockQuantity = getTotalStockQuantity();
            
            return new InventoryOverviewResponse(
                (int) totalProducts,
                (int) totalVariants,
                totalStockValue,
                lowStockItems,
                outOfStockItems,
                overstockItems,
                totalStockQuantity,
                LocalDateTime.now()
            );
            
        } catch (Exception e) {
            log.error("Failed to get inventory overview: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get inventory overview: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LowStockAlertResponse> getLowStockAlerts(Pageable pageable, Integer threshold) {
        try {
            int actualThreshold = threshold != null ? threshold : 10;
            
            // Get variants with low stock
            Page<ProductVariant> lowStockVariants = productVariantRepository.findLowStockVariants(actualThreshold, pageable);
            
            return lowStockVariants.map(variant -> {
                LocalDateTime lastSold = inventoryMovementRepository.getLastSaleDate(variant.getId());
                return inventoryMapper.toLowStockAlertResponse(variant, lastSold);
            });
            
        } catch (Exception e) {
            log.error("Failed to get low stock alerts: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get low stock alerts: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<OutOfStockResponse> getOutOfStockItems(Pageable pageable) {
        try {
            // Get variants with zero stock
            Page<ProductVariant> outOfStockVariants = productVariantRepository.findOutOfStockVariants(pageable);
            
            return outOfStockVariants.map(variant -> {
                LocalDateTime lastSold = inventoryMovementRepository.getLastSaleDate(variant.getId());
                LocalDateTime from = lastSold != null ? lastSold.minusDays(30) : LocalDateTime.now().minusDays(30);
                LocalDateTime to = LocalDateTime.now();
                Double averageDailySales = inventoryMovementRepository.getAverageDailySales(variant.getId(), from, to);
                return inventoryMapper.toOutOfStockResponse(variant, lastSold, averageDailySales);
            });
            
        } catch (Exception e) {
            log.error("Failed to get out of stock items: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get out of stock items: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<StockMovementResponse> getStockMovementHistory(Pageable pageable, UUID productId, UUID variantId, 
                                                             LocalDateTime from, LocalDateTime to) {
        try {
            Page<InventoryMovement> movements;
            
            if (variantId != null) {
                if (from != null && to != null) {
                    movements = inventoryMovementRepository.findByVariantAndDateRange(variantId, from, to, pageable);
                } else {
                    movements = inventoryMovementRepository.findByVariantId(variantId, pageable);
                }
            } else if (productId != null) {
                if (from != null && to != null) {
                    movements = inventoryMovementRepository.findByProductAndDateRange(productId, from, to, pageable);
                } else {
                    movements = inventoryMovementRepository.findByProductId(productId, pageable);
                }
            } else {
                if (from != null && to != null) {
                    movements = inventoryMovementRepository.findByDateRange(from, to, pageable);
                } else {
                    movements = inventoryMovementRepository.findAll(pageable);
                }
            }
            
            return movements.map(inventoryMapper::toStockMovementResponse);
            
        } catch (Exception e) {
            log.error("Failed to get stock movement history: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get stock movement history: " + e.getMessage());
        }
    }
    
    // Helper methods
    private BigDecimal calculateTotalStockValue() {
        try {
            // Use efficient database query to calculate total stock value
            return productVariantRepository.getTotalStockValue();
        } catch (Exception e) {
            log.error("Failed to calculate total stock value: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    private int getLowStockItemsCount() {
        return productVariantRepository.countLowStockVariants(10);
    }
    
    private int getOutOfStockItemsCount() {
        return productVariantRepository.countOutOfStockVariants();
    }
    
    private int getOverstockItemsCount() {
        try {
            // Define overstock threshold (e.g., variants with stock > 1000 or > 10x low stock threshold)
            List<ProductVariant> variants = productVariantRepository.findAll().stream()
                .filter(variant -> variant.getIsActive())
                .collect(Collectors.toList());
            
            int overstockCount = 0;
            for (ProductVariant variant : variants) {
                // Consider overstock if stock is more than 10x the low stock threshold
                // or if stock is more than 1000 units
                int overstockThreshold = Math.max(variant.getLowStockThreshold() * 10, 1000);
                if (variant.getStockQuantity() > overstockThreshold) {
                    overstockCount++;
                }
            }
            
            return overstockCount;
        } catch (Exception e) {
            log.error("Failed to calculate overstock items count: {}", e.getMessage());
            return 0;
        }
    }
    
    private int getTotalStockQuantity() {
        return productVariantRepository.getTotalStockQuantity();
    }
    
    @Override
    @Transactional
    public BulkStockUpdateResponse performBulkStockUpdate(BulkStockUpdateRequest request) {
        try {
            List<BulkStockUpdateResponse.StockUpdateResult> updatedVariants = new ArrayList<>();
            List<BulkStockUpdateResponse.StockUpdateResult> failedVariants = new ArrayList<>();
            
            for (BulkStockUpdateRequest.StockUpdateItem update : request.getUpdates()) {
                try {
                    UUID variantId = UUID.fromString(update.getVariantId());
                    ProductVariant variant = productVariantRepository.findById(variantId)
                        .orElseThrow(() -> new NotFoundException("Variant not found with ID: " + variantId));
                    
                    int previousStock = variant.getStockQuantity();
                    int newStock = update.getStockQuantity();
                    
                    // Update variant stock
                    variant.setStockQuantity(newStock);
                    if (update.getLowStockThreshold() != null) {
                        variant.setLowStockThreshold(update.getLowStockThreshold());
                    }
                    productVariantRepository.save(variant);
                    
                    // Record movement
                    recordStockMovement(
                        variant.getProduct().getId(),
                        variantId,
                        InventoryMovement.MovementType.RESTOCK.name(),
                        newStock - previousStock,
                        previousStock,
                        newStock,
                        update.getReason() != null ? update.getReason() : request.getReason(),
                        null,
                        UUID.fromString(request.getCreatedBy())
                    );
                    
                    updatedVariants.add(new BulkStockUpdateResponse.StockUpdateResult(
                        variantId, previousStock, newStock, "SUCCESS", null
                    ));
                    
                } catch (Exception e) {
                    log.error("Failed to update variant {}: {}", update.getVariantId(), e.getMessage());
                    failedVariants.add(new BulkStockUpdateResponse.StockUpdateResult(
                        UUID.fromString(update.getVariantId()), null, null, "FAILED", e.getMessage()
                    ));
                }
            }
            
            return new BulkStockUpdateResponse(
                updatedVariants.size(),
                failedVariants.size(),
                updatedVariants,
                failedVariants
            );
            
        } catch (Exception e) {
            log.error("Failed to perform bulk stock update: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to perform bulk stock update: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public StockAdjustmentResponse performStockAdjustment(StockAdjustmentRequest request) {
        try {
            UUID variantId = UUID.fromString(request.getVariantId());
            ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new NotFoundException("Variant not found with ID: " + variantId));
            
            int previousStock = variant.getStockQuantity();
            int newStock;
            int adjustmentQuantity;
            
            switch (request.getAdjustmentType()) {
                case ADD:
                    adjustmentQuantity = request.getQuantity();
                    newStock = previousStock + adjustmentQuantity;
                    break;
                case REMOVE:
                    adjustmentQuantity = -request.getQuantity();
                    newStock = Math.max(0, previousStock - request.getQuantity());
                    break;
                case SET:
                    adjustmentQuantity = request.getQuantity() - previousStock;
                    newStock = request.getQuantity();
                    break;
                default:
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid adjustment type");
            }
            
            // Update variant stock
            variant.setStockQuantity(newStock);
            productVariantRepository.save(variant);
            
            // Record movement
            InventoryMovement movement = InventoryMovement.builder()
                .product(variant.getProduct())
                .variant(variant)
                .movementType(InventoryMovement.MovementType.ADJUSTMENT)
                .quantity(adjustmentQuantity)
                .previousStock(previousStock)
                .newStock(newStock)
                .reason(request.getReason())
                .createdBy(UUID.fromString(request.getCreatedBy()))
                .build();
            
            InventoryMovement savedMovement = inventoryMovementRepository.save(movement);
            
            return new StockAdjustmentResponse(
                variantId,
                previousStock,
                newStock,
                request.getAdjustmentType().name(),
                adjustmentQuantity,
                savedMovement.getId()
            );
            
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to perform stock adjustment: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to perform stock adjustment: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public InventoryReportResponse generateInventoryReport(LocalDateTime from, LocalDateTime to) {
        try {
            // Get all variants for the period
            List<ProductVariant> variants = productVariantRepository.findAll();
            
            int totalSales = 0;
            int totalReturns = 0;
            int totalAdjustments = 0;
            List<InventoryReportResponse.TopSellingVariant> topSellingVariants = new ArrayList<>();
            List<InventoryReportResponse.SlowMovingVariant> slowMovingVariants = new ArrayList<>();
            
            for (ProductVariant variant : variants) {
                // Get sales data
                Integer sales = inventoryMovementRepository.getTotalSalesQuantity(variant.getId(), from, to);
                Integer returns = inventoryMovementRepository.getTotalReturnsQuantity(variant.getId(), from, to);
                Integer adjustments = inventoryMovementRepository.getTotalAdjustmentsQuantity(variant.getId(), from, to);
                
                totalSales += sales != null ? sales : 0;
                totalReturns += returns != null ? returns : 0;
                totalAdjustments += adjustments != null ? adjustments : 0;
                
                // Calculate revenue (simplified)
                BigDecimal revenue = variant.getPrice().multiply(BigDecimal.valueOf(sales != null ? sales : 0));
                
                // Top selling variants (simplified logic)
                if (sales != null && sales > 0) {
                    topSellingVariants.add(new InventoryReportResponse.TopSellingVariant(
                        variant.getId(),
                        variant.getSku(),
                        variant.getProduct().getTitle(),
                        sales,
                        revenue
                    ));
                }
                
                // Slow moving variants (simplified logic)
                if (sales == null || sales < 5) {
                    int daysInStock = (int) ChronoUnit.DAYS.between(variant.getCreatedAt(), LocalDateTime.now());
                    slowMovingVariants.add(new InventoryReportResponse.SlowMovingVariant(
                        variant.getId(),
                        variant.getSku(),
                        variant.getProduct().getTitle(),
                        sales != null ? sales : 0,
                        daysInStock
                    ));
                }
            }
            
            // Sort and limit results
            topSellingVariants = topSellingVariants.stream()
                .sorted((a, b) -> Integer.compare(b.salesQuantity(), a.salesQuantity()))
                .limit(10)
                .collect(Collectors.toList());
                
            slowMovingVariants = slowMovingVariants.stream()
                .sorted((a, b) -> Integer.compare(b.daysInStock(), a.daysInStock()))
                .limit(10)
                .collect(Collectors.toList());
            
            int netStockChange = totalSales - totalReturns + totalAdjustments;
            
            return new InventoryReportResponse(
                new InventoryReportResponse.Period(from, to),
                new InventoryReportResponse.Summary(
                    totalSales,
                    totalReturns,
                    totalAdjustments,
                    netStockChange,
                    topSellingVariants,
                    slowMovingVariants
                )
            );
            
        } catch (Exception e) {
            log.error("Failed to generate inventory report: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate inventory report: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public InventoryAlertConfigResponse getAlertConfig() {
        try {
            InventoryAlertConfig config = alertConfigRepository.findActiveConfig()
                .orElseGet(() -> {
                    // Create default config if none exists
                    InventoryAlertConfig defaultConfig = InventoryAlertConfig.builder().build();
                    return alertConfigRepository.save(defaultConfig);
                });
            
            return inventoryMapper.toInventoryAlertConfigResponse(config);
            
        } catch (Exception e) {
            log.error("Failed to get alert config: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get alert config: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public InventoryAlertConfigResponse updateAlertConfig(UpdateAlertConfigRequest request) {
        try {
            InventoryAlertConfig config = alertConfigRepository.findActiveConfig()
                .orElseGet(() -> InventoryAlertConfig.builder().build());
            
            config.setGlobalLowStockThreshold(request.getGlobalLowStockThreshold());
            config.setEnableEmailAlerts(request.getEnableEmailAlerts());
            config.setEnableDashboardAlerts(request.getEnableDashboardAlerts());
            config.setAlertFrequency(InventoryAlertConfig.AlertFrequency.valueOf(request.getAlertFrequency().name()));
            config.setAlertRecipients(request.getAlertRecipients());
            
            InventoryAlertConfig savedConfig = alertConfigRepository.save(config);
            
            return inventoryMapper.toInventoryAlertConfigResponse(savedConfig);
            
        } catch (Exception e) {
            log.error("Failed to update alert config: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update alert config: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public void recordStockMovement(UUID productId, UUID variantId, String movementType, 
                                  Integer quantity, Integer previousStock, Integer newStock, 
                                  String reason, String referenceId, UUID createdBy) {
        try {
            ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new NotFoundException("Variant not found with ID: " + variantId));
            
            InventoryMovement movement = InventoryMovement.builder()
                .product(variant.getProduct())
                .variant(variant)
                .movementType(InventoryMovement.MovementType.valueOf(movementType))
                .quantity(quantity)
                .previousStock(previousStock)
                .newStock(newStock)
                .reason(reason)
                .referenceId(referenceId)
                .createdBy(createdBy)
                .build();
            
            inventoryMovementRepository.save(movement);
            
        } catch (Exception e) {
            log.error("Failed to record stock movement: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to record stock movement: " + e.getMessage());
        }
    }
}
