package com.importer_ecommerce.importEcommerce.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for conflict errors (duplicate resources, etc.)
 */
public class ConflictException extends ApiException {
    
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, "CONFLICT", message);
    }
    
    public ConflictException(String resource, String identifier) {
        super(HttpStatus.CONFLICT, "CONFLICT", 
            String.format("%s with identifier '%s' already exists", resource, identifier));
    }
    
    public ConflictException(String resource, String identifier, String field) {
        super(HttpStatus.CONFLICT, "CONFLICT", 
            String.format("%s with %s '%s' already exists", resource, field, identifier));
    }
}
