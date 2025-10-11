package com.importer_ecommerce.importEcommerce.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for forbidden access errors
 */
public class ForbiddenException extends ApiException {
    
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, "FORBIDDEN", message);
    }
    
    public ForbiddenException() {
        super(HttpStatus.FORBIDDEN, "FORBIDDEN", "Access denied");
    }
    
    public ForbiddenException(String resource, String action) {
        super(HttpStatus.FORBIDDEN, "FORBIDDEN", 
            String.format("You don't have permission to %s %s", action, resource));
    }
}
