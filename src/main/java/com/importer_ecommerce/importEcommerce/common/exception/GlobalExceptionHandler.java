package com.importer_ecommerce.importEcommerce.common.exception;

import com.importer_ecommerce.importEcommerce.common.constants.ErrorCodes;
import com.importer_ecommerce.importEcommerce.common.utils.ApiResponse;
import com.importer_ecommerce.importEcommerce.common.utils.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.TransactionSystemException;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {

        log.warn("Business exception: {} - {}", ex.getErrorCode(), ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                ex.getMessage(),
                ex.getErrorCode(),
                request.getRequestURI()
        );

        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        log.warn("Validation failed for request: {} - {} errors",
                request.getRequestURI(), validationErrors.size());

        ApiResponse<Void> response = ApiResponse.validationError(
                "Validation failed",
                ErrorCodes.VALIDATION_FAILED,
                request.getRequestURI(),
                validationErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {

        List<ValidationError> validationErrors = ex.getConstraintViolations()
                .stream()
                .map(violation -> new ValidationError(
                        violation.getPropertyPath().toString(),
                        violation.getInvalidValue(),
                        violation.getMessage()
                ))
                .collect(Collectors.toList());

        log.warn("Constraint violation for request: {}", request.getRequestURI());

        ApiResponse<Void> response = ApiResponse.validationError(
                "Validation failed",
                ErrorCodes.VALIDATION_FAILED,
                request.getRequestURI(),
                validationErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        log.warn("Invalid JSON format in request: {}", request.getRequestURI());

        ApiResponse<Void> response = ApiResponse.error(
                "Invalid JSON format",
                ErrorCodes.INVALID_INPUT_FORMAT,
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

        log.warn("Method not supported: {} for {}", ex.getMethod(), request.getRequestURI());

        ApiResponse<Void> response = ApiResponse.error(
                String.format("HTTP method '%s' is not supported for this endpoint", ex.getMethod()),
                "METHOD_NOT_SUPPORTED",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {

        log.warn("Access denied for request: {}", request.getRequestURI());

        ApiResponse<Void> response = ApiResponse.error(
                "Access denied - insufficient permissions",
                ErrorCodes.INSUFFICIENT_PERMISSIONS,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // Handle JPA/Database related exceptions
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        log.error("Data integrity violation for request: {} - {}", request.getRequestURI(), ex.getMessage(), ex);

        String message = "Data integrity constraint violation";
        if (ex.getCause() instanceof ConstraintViolationException) {
            message = "Duplicate entry or constraint violation";
        }

        ApiResponse<Void> response = ApiResponse.error(
                message,
                ErrorCodes.DATABASE_ERROR,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(JpaSystemException.class)
    public ResponseEntity<ApiResponse<Void>> handleJpaSystemException(
            JpaSystemException ex, HttpServletRequest request) {

        log.error("JPA system error for request: {} - {}", request.getRequestURI(), ex.getMessage(), ex);

        ApiResponse<Void> response = ApiResponse.error(
                "A database system error occurred. Please try again later.",
                ErrorCodes.DATABASE_ERROR,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ApiResponse<Void>> handleTransactionSystemException(
            TransactionSystemException ex, HttpServletRequest request) {

        log.error("Transaction system error for request: {} - {}", request.getRequestURI(), ex.getMessage(), ex);

        ApiResponse<Void> response = ApiResponse.error(
                "A transaction error occurred. Please try again later.",
                ErrorCodes.DATABASE_ERROR,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLockingFailureException(
            OptimisticLockingFailureException ex, HttpServletRequest request) {

        log.warn("Optimistic locking failure for request: {} - {}", request.getRequestURI(), ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                "The resource has been modified by another user. Please refresh and try again.",
                "OPTIMISTIC_LOCK_FAILURE",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, HttpServletRequest request) {

        log.error("Unexpected error for request: {} - {}", request.getRequestURI(), ex.getMessage(), ex);

        ApiResponse<Void> response = ApiResponse.error(
                "An unexpected error occurred. Please try again later.",
                ErrorCodes.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}