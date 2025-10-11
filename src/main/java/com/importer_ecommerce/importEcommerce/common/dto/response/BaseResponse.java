package com.importer_ecommerce.importEcommerce.common.dto.response;

import java.time.LocalDateTime;

/**
 * Base response record for all API responses
 */
public record BaseResponse(
    boolean success,
    String message,
    LocalDateTime timestamp
) {
    public static BaseResponse success(String message) {
        return new BaseResponse(true, message, LocalDateTime.now());
    }
    
    public static BaseResponse error(String message) {
        return new BaseResponse(false, message, LocalDateTime.now());
    }
}
