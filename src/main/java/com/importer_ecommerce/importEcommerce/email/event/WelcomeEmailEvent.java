package com.importer_ecommerce.importEcommerce.email.event;

import lombok.Getter;

@Getter
public class WelcomeEmailEvent extends EmailEvent {
    private final String customerName;
    
    public WelcomeEmailEvent(String to, String customerName) {
        super(to, "Welcome to ImportEcommerce!", "welcome", null);
        this.customerName = customerName;
    }
    
    @Override
    public Object getTemplateData() {
        return new WelcomeEmailData(customerName);
    }
    
    public static class WelcomeEmailData {
        private final String customerName;
        
        public WelcomeEmailData(String customerName) {
            this.customerName = customerName;
        }
        
        public String getCustomerName() {
            return customerName;
        }
    }
}

