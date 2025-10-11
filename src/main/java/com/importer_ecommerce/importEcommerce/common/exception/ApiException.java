package com.importer_ecommerce.importEcommerce.common.exception;

import com.importer_ecommerce.importEcommerce.common.dto.response.FieldError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Base exception for all API-related errors
 * Provides consistent error handling across the application
 */
@Getter
public class ApiException extends RuntimeException {
    
    private final HttpStatus httpStatus;
    private final String errorCode;
    private final List<FieldError> fieldErrors;
    
    /**
     * Constructor for simple error message
     */
    public ApiException(String message) {
        super(message);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.errorCode = "INTERNAL_ERROR";
        this.fieldErrors = null;
    }
    
    /**
     * Constructor with HTTP status
     */
    public ApiException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = httpStatus.name();
        this.fieldErrors = null;
    }
    
    /**
     * Constructor with HTTP status and error code
     */
    public ApiException(HttpStatus httpStatus, String errorCode, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.fieldErrors = null;
    }
    
    /**
     * Constructor with field errors for validation
     */
    public ApiException(HttpStatus httpStatus, String errorCode, String message, List<FieldError> fieldErrors) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.fieldErrors = fieldErrors;
    }
    
    /**
     * Constructor with cause
     */
    public ApiException(HttpStatus httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = httpStatus.name();
        this.fieldErrors = null;
    }
}
