package com.importer_ecommerce.importEcommerce.common.exception;

import com.importer_ecommerce.importEcommerce.common.dto.response.FieldError;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Exception for validation errors
 */
public class ValidationException extends ApiException {
    
    public ValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }
    
    public ValidationException(String message, List<FieldError> fieldErrors) {
        super(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, fieldErrors);
    }
    
    public ValidationException(String field, String message) {
        super(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", 
            List.of(FieldError.of(field, message)));
    }
    
    public ValidationException(String field, String message, Object rejectedValue) {
        super(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", 
            List.of(FieldError.of(field, message, rejectedValue)));
    }
}
