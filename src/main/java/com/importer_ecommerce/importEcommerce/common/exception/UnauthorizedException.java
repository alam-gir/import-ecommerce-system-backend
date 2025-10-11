package com.importer_ecommerce.importEcommerce.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for unauthorized access errors
 */
public class UnauthorizedException extends ApiException {
    
    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message);
    }
    
    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authentication required");
    }
}
