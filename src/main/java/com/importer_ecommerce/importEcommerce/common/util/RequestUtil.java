package com.importer_ecommerce.importEcommerce.common.util;

import com.importer_ecommerce.importEcommerce.common.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


/**
 * Utility class for handling API responses
 */
public class RequestUtil {
    
    /**
     * Create success response with data
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        ApiResponse<T> response = ApiResponse.success(data);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Create success response with message and data
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        ApiResponse<T> response = ApiResponse.success(message, data);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Create error response with message
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message) {
        ApiResponse<T> response = ApiResponse.error(message);
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * Create error response with message and HTTP status
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, HttpStatus status) {
        ApiResponse<T> response = ApiResponse.error(message);
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Create error response with message, data and HTTP status
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, T data, HttpStatus status) {
        ApiResponse<T> response = ApiResponse.error(message, data);
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Create unauthorized response
     */
    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message) {
        return error(message, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Create forbidden response
     */
    public static <T> ResponseEntity<ApiResponse<T>> forbidden(String message) {
        return error(message, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Create not found response
     */
    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return error(message, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Create internal server error response
     */
    public static <T> ResponseEntity<ApiResponse<T>> internalError(String message) {
        return error(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
