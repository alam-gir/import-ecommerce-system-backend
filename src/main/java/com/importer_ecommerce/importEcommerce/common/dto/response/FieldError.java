package com.importer_ecommerce.importEcommerce.common.dto.response;

/**
 * Field-specific error for validation and form errors
 */
public record FieldError(
    String field,
    String message,
    Object rejectedValue
) {
    
    /**
     * Create field error without rejected value
     */
    public static FieldError of(String field, String message) {
        return new FieldError(field, message, null);
    }
    
    /**
     * Create field error with rejected value
     */
    public static FieldError of(String field, String message, Object rejectedValue) {
        return new FieldError(field, message, rejectedValue);
    }
}
