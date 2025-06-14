package com.importer_ecommerce.importEcommerce.common.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED.toString(), HttpStatus.UNAUTHORIZED);
    }
}
