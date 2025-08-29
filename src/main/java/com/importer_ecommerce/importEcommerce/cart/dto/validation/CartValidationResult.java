package com.importer_ecommerce.importEcommerce.cart.dto.validation;

import java.util.List;

public record CartValidationResult(
    boolean isValid,
    List<ValidationError> errors,
    String userFriendlyMessage
) {
    
    public CartValidationResult(boolean isValid, List<ValidationError> errors) {
        this(isValid, errors, generateUserFriendlyMessage(errors));
    }
    
    private static String generateUserFriendlyMessage(List<ValidationError> errors) {
        if (errors == null || errors.isEmpty()) {
            return "✅ Cart is valid and ready!";
        }
        
        if (errors.size() == 1) {
            return errors.get(0).message();
        }
        
        return String.format("⚠️ %d issues found in your cart. Please review and fix them.", errors.size());
    }
}
