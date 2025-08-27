package com.importer_ecommerce.importEcommerce.email.service;

import com.importer_ecommerce.importEcommerce.email.event.EmailEvent;

public interface EmailService {
    void sendEmail(EmailEvent emailEvent);
    void sendWelcomeEmail(String to, String customerName);
    void sendOtpEmail(String to, String otp, String purpose);
    void sendProfileUpdateEmail(String to, String customerName, String updatedField);
}

