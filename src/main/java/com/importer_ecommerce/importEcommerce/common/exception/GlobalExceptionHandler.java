package com.importer_ecommerce.importEcommerce.common.exception;

import com.importer_ecommerce.importEcommerce.common.dto.response.ApiResponse;
import com.importer_ecommerce.importEcommerce.common.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Global exception handler for all API exceptions
 * Provides consistent error responses across the application
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle custom ApiException
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleApiException(ApiException ex, HttpServletRequest request) {
        log.warn("API Exception: {} - {}", ex.getMessage(), request.getRequestURI());
        
        if (ex.getFieldErrors() != null && !ex.getFieldErrors().isEmpty()) {
            return RequestUtil.error(ex.getMessage(), ex.getFieldErrors(), ex.getHttpStatus());
        }
        
        return RequestUtil.error(ex.getMessage(), ex.getHttpStatus());
    }

    /**
     * Handle validation errors from @Valid annotation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation Error: {}", ex.getMessage());
        
        List<com.importer_ecommerce.importEcommerce.common.dto.response.FieldError> fieldErrors = new ArrayList<>();
        for (org.springframework.validation.FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.add(com.importer_ecommerce.importEcommerce.common.dto.response.FieldError.of(
                error.getField(),
                error.getDefaultMessage(),
                error.getRejectedValue()
            ));
        }
        
        return RequestUtil.validationError("Validation failed", fieldErrors);
    }

    /**
     * Handle binding errors
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Object>> handleBindException(BindException ex) {
        log.warn("Bind Exception: {}", ex.getMessage());
        
        List<com.importer_ecommerce.importEcommerce.common.dto.response.FieldError> fieldErrors = new ArrayList<>();
        for (org.springframework.validation.FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.add(com.importer_ecommerce.importEcommerce.common.dto.response.FieldError.of(
                error.getField(),
                error.getDefaultMessage(),
                error.getRejectedValue()
            ));
        }
        
        return RequestUtil.validationError("Binding failed", fieldErrors);
    }

    /**
     * Handle constraint violation exceptions
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Constraint Violation: {}", ex.getMessage());
        
        List<com.importer_ecommerce.importEcommerce.common.dto.response.FieldError> fieldErrors = new ArrayList<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        
        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            fieldErrors.add(com.importer_ecommerce.importEcommerce.common.dto.response.FieldError.of(
                fieldName,
                violation.getMessage(),
                violation.getInvalidValue()
            ));
        }
        
        return RequestUtil.validationError("Constraint violation", fieldErrors);
    }

    /**
     * Handle authentication exceptions
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(Exception ex) {
        log.warn("Authentication Error: {}", ex.getMessage());
        return RequestUtil.unauthorized("Authentication failed. Please provide valid credentials.");
    }

    /**
     * Handle access denied exceptions
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access Denied: {}", ex.getMessage());
        return RequestUtil.forbidden("Access denied. You don't have permission to access this resource.");
    }

    /**
     * Handle missing request parameters
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingParameterException(MissingServletRequestParameterException ex) {
        log.warn("Missing Parameter: {}", ex.getMessage());
        return RequestUtil.fieldError(ex.getParameterName(), "Required parameter is missing");
    }

    /**
     * Handle method argument type mismatch
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("Type Mismatch: {}", ex.getMessage());
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s", 
            ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());
        return RequestUtil.fieldError(ex.getName(), message, ex.getValue());
    }

    /**
     * Handle HTTP message not readable (malformed JSON)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("Malformed Request: {}", ex.getMessage());
        return RequestUtil.error("Malformed request body. Please check your JSON format.", HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle unsupported HTTP methods
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("Method Not Supported: {}", ex.getMessage());
        String message = String.format("Method '%s' is not supported for this endpoint. Supported methods: %s", 
            ex.getMethod(), String.join(", ", ex.getSupportedMethods()));
        return RequestUtil.error(message, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Handle 404 errors
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.warn("No Handler Found: {}", ex.getMessage());
        return RequestUtil.notFound("The requested resource was not found.");
    }

    /**
     * Handle database integrity violations
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Data Integrity Violation: {}", ex.getMessage());
        
        String message = "Data integrity violation occurred.";
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("duplicate key")) {
                message = "A record with this information already exists.";
            } else if (ex.getMessage().contains("foreign key constraint")) {
                message = "Cannot perform this action due to related data constraints.";
            }
        }
        
        return RequestUtil.conflict(message);
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal Argument: {}", ex.getMessage());
        return RequestUtil.error("Invalid argument provided: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle illegal state exceptions
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalStateException(IllegalStateException ex) {
        log.warn("Illegal State: {}", ex.getMessage());
        return RequestUtil.error("Invalid state: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle null pointer exceptions
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Object>> handleNullPointerException(NullPointerException ex) {
        log.error("Null Pointer Exception: {}", ex.getMessage(), ex);
        return RequestUtil.internalError("An unexpected error occurred. Please try again.");
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred: {} - {}", ex.getMessage(), request.getRequestURI(), ex);
        return RequestUtil.internalError("An unexpected error occurred. Please try again later.");
    }
}
