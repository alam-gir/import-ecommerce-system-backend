package com.importer_ecommerce.importEcommerce.modules.inventory.entity;

import com.importer_ecommerce.importEcommerce.common.entity.AuditableEntity;
import com.importer_ecommerce.importEcommerce.modules.product.entity.Product;
import com.importer_ecommerce.importEcommerce.modules.product.entity.ProductVariant;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entity for tracking inventory movements and stock changes
 * Records all stock adjustments, sales, returns, and other movements
 */
@Entity
@Table(name = "inventory_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryMovement extends AuditableEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity; // Positive for additions, negative for deductions
    
    @Column(name = "previous_stock", nullable = false)
    private Integer previousStock;
    
    @Column(name = "new_stock", nullable = false)
    private Integer newStock;
    
    @Column(name = "reason", length = 500)
    private String reason;
    
    @Column(name = "reference_id")
    private String referenceId; // Order ID, adjustment ID, etc.
    
    @Column(name = "created_by")
    private UUID createdBy; // User who initiated the movement
    
    /**
     * Movement types for inventory tracking
     */
    public enum MovementType {
        SALE,           // Stock sold to customer
        RETURN,         // Customer return
        ADJUSTMENT,     // Manual adjustment
        RESTOCK,        // Supplier restock
        TRANSFER,       // Transfer between locations
        DAMAGE,         // Damaged goods
        THEFT,          // Theft/loss
        EXPIRED,        // Expired goods
        SYSTEM_ADJUSTMENT // System-generated adjustment
    }
}
