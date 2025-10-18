package com.importer_ecommerce.importEcommerce.modules.inventory.dto.response;

import java.util.List;

/**
 * Response DTO for inventory alert configuration
 * Contains current alert settings and preferences
 */
public record InventoryAlertConfigResponse(
    Integer globalLowStockThreshold,
    Boolean enableEmailAlerts,
    Boolean enableDashboardAlerts,
    String alertFrequency,
    List<String> alertRecipients
) {}
