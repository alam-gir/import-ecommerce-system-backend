package com.importer_ecommerce.importEcommerce.common.constants;

public final class ErrorCodes {
    // Authentication & Authorization
    public static final String INVALID_CREDENTIALS = "AUTH_001";
    public static final String TOKEN_EXPIRED = "AUTH_002";
    public static final String TOKEN_INVALID = "AUTH_003";
    public static final String INSUFFICIENT_PERMISSIONS = "AUTH_004";
    public static final String ACCOUNT_LOCKED = "AUTH_005";

    // Validation
    public static final String VALIDATION_FAILED = "VAL_001";
    public static final String INVALID_INPUT_FORMAT = "VAL_002";
    public static final String MISSING_REQUIRED_FIELD = "VAL_003";

    // Business Logic
    public static final String RESOURCE_NOT_FOUND = "BUS_001";
    public static final String DUPLICATE_RESOURCE = "BUS_002";
    public static final String INVALID_OPERATION = "BUS_003";
    public static final String BUSINESS_RULE_VIOLATION = "BUS_004";

    // System Errors
    public static final String INTERNAL_SERVER_ERROR = "SYS_001";
    public static final String DATABASE_ERROR = "SYS_002";
    public static final String EXTERNAL_SERVICE_ERROR = "SYS_003";
    public static final String RATE_LIMIT_EXCEEDED = "SYS_004";
    
    // HTTP Errors
    public static final String NOT_FOUND = "HTTP_001";
    public static final String METHOD_NOT_ALLOWED = "HTTP_002";
    public static final String INVALID_REQUEST = "HTTP_003";

    private ErrorCodes() {} // Prevent instantiation
}
