package com.importer_ecommerce.importEcommerce.email.event;

import lombok.Getter;

@Getter
public class ProfileUpdateEmailEvent extends EmailEvent {
    private final String customerName;
    private final String updatedField;
    
    public ProfileUpdateEmailEvent(String to, String customerName, String updatedField) {
        super(to, "Profile Updated - ImportEcommerce", "profile-update", null);
        this.customerName = customerName;
        this.updatedField = updatedField;
    }
    
    @Override
    public Object getTemplateData() {
        return new ProfileUpdateEmailData(customerName, updatedField);
    }
    
    public static class ProfileUpdateEmailData {
        private final String customerName;
        private final String updatedField;
        
        public ProfileUpdateEmailData(String customerName, String updatedField) {
            this.customerName = customerName;
            this.updatedField = updatedField;
        }
        
        public String getCustomerName() {
            return customerName;
        }
        
        public String getUpdatedField() {
            return updatedField;
        }
    }
}

