package com.importer_ecommerce.importEcommerce.common.exception;

import org.springframework.http.HttpStatus;

public class  NotFoundException extends BusinessException {
    public NotFoundException(String resourceName, String identifier) {
        super(String.format("%s not found with : %s", resourceName, identifier), HttpStatus.NOT_FOUND.toString());
    }
}
