package com.importer_ecommerce.importEcommerce.modules.inventory.entity;

import com.importer_ecommerce.importEcommerce.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Entity for inventory alert configuration
 * Stores global settings for low stock alerts and notifications
 */
@Entity
@Table(name = "inventory_alert_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryAlertConfig extends AuditableEntity {
    
    @Column(name = "global_low_stock_threshold", nullable = false)
    @Builder.Default
    private Integer globalLowStockThreshold = 10;
    
    @Column(name = "enable_email_alerts", nullable = false)
    @Builder.Default
    private Boolean enableEmailAlerts = true;
    
    @Column(name = "enable_dashboard_alerts", nullable = false)
    @Builder.Default
    private Boolean enableDashboardAlerts = true;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "alert_frequency", nullable = false)
    @Builder.Default
    private AlertFrequency alertFrequency = AlertFrequency.DAILY;
    
    @ElementCollection
    @CollectionTable(name = "inventory_alert_recipients", joinColumns = @JoinColumn(name = "config_id"))
    @Column(name = "email")
    @Builder.Default
    private List<String> alertRecipients = List.of();
    
    /**
     * Alert frequency options
     */
    public enum AlertFrequency {
        HOURLY,
        DAILY,
        WEEKLY,
        MONTHLY
    }
}
