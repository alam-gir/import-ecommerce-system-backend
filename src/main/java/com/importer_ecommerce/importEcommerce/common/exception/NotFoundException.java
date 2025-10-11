package com.importer_ecommerce.importEcommerce.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for resource not found errors
 */
public class NotFoundException extends ApiException {
    
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "NOT_FOUND", message);
    }
    
    public NotFoundException(String resource, String identifier) {
        super(HttpStatus.NOT_FOUND, "NOT_FOUND", 
            String.format("%s with identifier '%s' not found", resource, identifier));
    }
    
    public NotFoundException(String resource, String identifier, String field) {
        super(HttpStatus.NOT_FOUND, "NOT_FOUND", 
            String.format("%s with %s '%s' not found", resource, field, identifier));
    }
}
