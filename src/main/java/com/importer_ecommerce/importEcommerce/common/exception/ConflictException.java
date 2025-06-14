package com.importer_ecommerce.importEcommerce.common.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BusinessException {
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT.toString(), HttpStatus.CONFLICT);
    }
}

