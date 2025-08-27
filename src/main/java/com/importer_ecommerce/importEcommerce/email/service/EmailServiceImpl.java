package com.importer_ecommerce.importEcommerce.email.service;

import com.importer_ecommerce.importEcommerce.email.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;
    private final EmailTemplateService templateService;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Override
    @Async
    public void sendEmail(EmailEvent emailEvent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(emailEvent.getTo());
            helper.setSubject(emailEvent.getSubject());
            
            String htmlContent = generateEmailContent(emailEvent);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", emailEvent.getTo());
            
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", emailEvent.getTo(), e);
        }
    }
    
    @Override
    @Async
    public void sendWelcomeEmail(String to, String customerName) {
        String htmlContent = templateService.generateWelcomeEmail(customerName);
        sendHtmlEmail(to, "Welcome to ImportEcommerce!", htmlContent);
    }
    
    @Override
    @Async
    public void sendOtpEmail(String to, String otp, String purpose) {
        String htmlContent = templateService.generateOtpEmail(otp, purpose);
        sendHtmlEmail(to, "Your Verification Code - ImportEcommerce", htmlContent);
    }
    
    @Override
    @Async
    public void sendProfileUpdateEmail(String to, String customerName, String updatedField) {
        String htmlContent = templateService.generateProfileUpdateEmail(customerName, updatedField);
        sendHtmlEmail(to, "Profile Updated - ImportEcommerce", htmlContent);
    }
    
    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
            
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }
    
    private String generateEmailContent(EmailEvent emailEvent) {
        switch (emailEvent.getTemplateName()) {
            case "welcome":
                WelcomeEmailEvent welcomeEvent = (WelcomeEmailEvent) emailEvent;
                return templateService.generateWelcomeEmail(welcomeEvent.getCustomerName());
            case "otp":
                OtpEmailEvent otpEvent = (OtpEmailEvent) emailEvent;
                return templateService.generateOtpEmail(otpEvent.getOtp(), otpEvent.getPurpose());
            case "profile-update":
                ProfileUpdateEmailEvent profileEvent = (ProfileUpdateEmailEvent) emailEvent;
                return templateService.generateProfileUpdateEmail(profileEvent.getCustomerName(), profileEvent.getUpdatedField());
            default:
                throw new IllegalArgumentException("Unknown template: " + emailEvent.getTemplateName());
        }
    }
}
