package com.importer_ecommerce.importEcommerce.common.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Generic API response wrapper with comprehensive error and pagination support
 */
public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    LocalDateTime timestamp,
    List<FieldError> errors,
    PaginationResponse pagination
) {
    
    // Success responses
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data, LocalDateTime.now(), null, null);
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now(), null, null);
    }
    
    public static <T> ApiResponse<T> success(String message, T data, PaginationResponse pagination) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now(), null, pagination);
    }
    
    // Error responses
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), null, null);
    }
    
    public static <T> ApiResponse<T> error(String message, List<FieldError> errors) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), errors, null);
    }
    
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data, LocalDateTime.now(), null, null);
    }
    
    public static <T> ApiResponse<T> error(String message, T data, List<FieldError> errors) {
        return new ApiResponse<>(false, message, data, LocalDateTime.now(), errors, null);
    }
    
    // Validation error response
    public static <T> ApiResponse<T> validationError(String message, List<FieldError> errors) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), errors, null);
    }
    
    // Field error helpers
    public static <T> ApiResponse<T> fieldError(String field, String message) {
        return new ApiResponse<>(false, "Validation failed", null, LocalDateTime.now(), 
            List.of(FieldError.of(field, message)), null);
    }
    
    public static <T> ApiResponse<T> fieldError(String field, String message, Object rejectedValue) {
        return new ApiResponse<>(false, "Validation failed", null, LocalDateTime.now(), 
            List.of(FieldError.of(field, message, rejectedValue)), null);
    }
}
