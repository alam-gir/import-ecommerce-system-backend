package com.importer_ecommerce.importEcommerce.user.entity;

/**
 * User roles in the system
 */
public enum UserRole {
    ADMIN("Admin"),
    USER("User");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
