package com.importer_ecommerce.importEcommerce.common.util;

import com.importer_ecommerce.importEcommerce.common.dto.response.ApiResponse;
import com.importer_ecommerce.importEcommerce.common.dto.response.FieldError;
import com.importer_ecommerce.importEcommerce.common.dto.response.PaginationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Utility class for handling API responses with comprehensive error support
 */
public class RequestUtil {
    
    // Success responses
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        ApiResponse<T> response = ApiResponse.success(data);
        return ResponseEntity.ok(response);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        ApiResponse<T> response = ApiResponse.success(message, data);
        return ResponseEntity.ok(response);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data, PaginationResponse pagination) {
        ApiResponse<T> response = ApiResponse.success(message, data, pagination);
        return ResponseEntity.ok(response);
    }
    
    // Error responses
    public static <T> ResponseEntity<ApiResponse<T>> error(String message) {
        ApiResponse<T> response = ApiResponse.error(message);
        return ResponseEntity.badRequest().body(response);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, HttpStatus status) {
        ApiResponse<T> response = ApiResponse.error(message);
        return ResponseEntity.status(status).body(response);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, T data, HttpStatus status) {
        ApiResponse<T> response = ApiResponse.error(message, data);
        return ResponseEntity.status(status).body(response);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, List<FieldError> errors) {
        ApiResponse<T> response = ApiResponse.error(message, errors);
        return ResponseEntity.badRequest().body(response);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, List<FieldError> errors, HttpStatus status) {
        ApiResponse<T> response = ApiResponse.error(message, errors);
        return ResponseEntity.status(status).body(response);
    }
    
    // Validation error responses
    public static <T> ResponseEntity<ApiResponse<T>> validationError(String message, List<FieldError> errors) {
        ApiResponse<T> response = ApiResponse.validationError(message, errors);
        return ResponseEntity.badRequest().body(response);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> fieldError(String field, String message) {
        ApiResponse<T> response = ApiResponse.fieldError(field, message);
        return ResponseEntity.badRequest().body(response);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> fieldError(String field, String message, Object rejectedValue) {
        ApiResponse<T> response = ApiResponse.fieldError(field, message, rejectedValue);
        return ResponseEntity.badRequest().body(response);
    }
    
    // Specific HTTP status responses
    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message) {
        return error(message, HttpStatus.UNAUTHORIZED);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> forbidden(String message) {
        return error(message, HttpStatus.FORBIDDEN);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return error(message, HttpStatus.NOT_FOUND);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> internalError(String message) {
        return error(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> conflict(String message) {
        return error(message, HttpStatus.CONFLICT);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> unprocessableEntity(String message, List<FieldError> errors) {
        return error(message, errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
