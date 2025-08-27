package com.importer_ecommerce.importEcommerce.email.event;

import lombok.Getter;

@Getter
public class OtpEmailEvent extends EmailEvent {
    private final String otp;
    private final String purpose;
    
    public OtpEmailEvent(String to, String otp, String purpose) {
        super(to, "Your Verification Code - ImportEcommerce", "otp", null);
        this.otp = otp;
        this.purpose = purpose;
    }
    
    @Override
    public Object getTemplateData() {
        return new OtpEmailData(otp, purpose);
    }
    
    public static class OtpEmailData {
        private final String otp;
        private final String purpose;
        
        public OtpEmailData(String otp, String purpose) {
            this.otp = otp;
            this.purpose = purpose;
        }
        
        public String getOtp() {
            return otp;
        }
        
        public String getPurpose() {
            return purpose;
        }
    }
}

