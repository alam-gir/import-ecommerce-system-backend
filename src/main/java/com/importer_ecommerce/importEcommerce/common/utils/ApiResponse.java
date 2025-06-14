package com.importer_ecommerce.importEcommerce.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private String timestamp;
    private String path;
    private List<ValidationError> validationErrors;

    // Constructor for success responses
    public ApiResponse(T data) {
        this.success = true;
        this.data = data;
        this.timestamp = Instant.now().toString();
    }

    // Constructor for success with message
    public ApiResponse(T data, String message) {
        this.success = true;
        this.data = data;
        this.message = message;
        this.timestamp = Instant.now().toString();
    }

    // Constructor for error responses
    public ApiResponse(String message, String errorCode, String path) {
        this.success = false;
        this.message = message;
        this.errorCode = errorCode;
        this.path = path;
        this.timestamp = Instant.now().toString();
    }

    // Constructor for validation errors
    public ApiResponse(String message, String errorCode, String path, List<ValidationError> validationErrors) {
        this.success = false;
        this.message = message;
        this.errorCode = errorCode;
        this.path = path;
        this.validationErrors = validationErrors;
        this.timestamp = Instant.now().toString();
    }

    // Static factory methods for cleaner usage
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(data, message);
    }

    public static <T> ApiResponse<T> error(String message, String errorCode, String path) {
        return new ApiResponse<>(message, errorCode, path);
    }

    public static <T> ApiResponse<T> validationError(String message, String errorCode, String path, List<ValidationError> errors) {
        return new ApiResponse<>(message, errorCode, path, errors);
    }
}
