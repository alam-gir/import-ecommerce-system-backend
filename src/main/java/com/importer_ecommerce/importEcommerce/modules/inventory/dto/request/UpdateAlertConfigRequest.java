package com.importer_ecommerce.importEcommerce.modules.inventory.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/**
 * Request DTO for updating inventory alert configuration
 * Used to configure low stock alerts and notifications
 */
@Data
public class UpdateAlertConfigRequest {
    
    @NotNull(message = "Global low stock threshold is required")
    @Positive(message = "Threshold must be positive")
    private Integer globalLowStockThreshold;
    
    @NotNull(message = "Email alerts setting is required")
    private Boolean enableEmailAlerts;
    
    @NotNull(message = "Dashboard alerts setting is required")
    private Boolean enableDashboardAlerts;
    
    @NotNull(message = "Alert frequency is required")
    private AlertFrequency alertFrequency;
    
    @NotEmpty(message = "Alert recipients list cannot be empty")
    private List<@Email(message = "Invalid email format") String> alertRecipients;
    
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
