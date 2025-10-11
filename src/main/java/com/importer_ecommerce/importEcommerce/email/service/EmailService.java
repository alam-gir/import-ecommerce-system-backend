package com.importer_ecommerce.importEcommerce.email.service;

import com.importer_ecommerce.importEcommerce.email.dto.request.EmailRequest;
import com.importer_ecommerce.importEcommerce.email.dto.response.EmailResponse;
import com.importer_ecommerce.importEcommerce.email.template.EmailTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;

/**
 * Email service for sending emails
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final EmailTemplate emailTemplate;
    
    /**
     * Send simple text email
     */
    public EmailResponse sendSimpleEmail(EmailRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(request.getTo());
            message.setSubject(request.getSubject());
            message.setText(request.getContent());
            
            mailSender.send(message);
            
            log.info("Simple email sent successfully to: {}", request.getTo());
            return new EmailResponse(true, "Email sent successfully", LocalDateTime.now());
            
        } catch (Exception e) {
            log.error("Failed to send simple email to: {}", request.getTo(), e);
            return new EmailResponse(false, "Failed to send email: " + e.getMessage(), LocalDateTime.now());
        }
    }
    
    /**
     * Send HTML email
     */
    public EmailResponse sendHtmlEmail(EmailRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(request.getContent(), true);
            
            mailSender.send(message);
            
            log.info("HTML email sent successfully to: {}", request.getTo());
            return new EmailResponse(true, "Email sent successfully", LocalDateTime.now());
            
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to: {}", request.getTo(), e);
            return new EmailResponse(false, "Failed to send email: " + e.getMessage(), LocalDateTime.now());
        }
    }
    
    /**
     * Send OTP email asynchronously
     */
    @Async
    public void sendOtpEmailAsync(String to, String recipientName, String otp) {
        try {
            String subject = "Your OTP for Taqreem Ecommerce";
            String content = emailTemplate.generateOtpEmail(recipientName, otp);
            
            EmailRequest request = EmailRequest.builder()
                .to(to)
                .subject(subject)
                .content(content)
                .recipientName(recipientName)
                .build();
            
            sendHtmlEmail(request);
            
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", to, e);
        }
    }
    
    /**
     * Send welcome email
     */
    public EmailResponse sendWelcomeEmail(String to, String recipientName) {
        try {
            String subject = "Welcome to Taqreem Ecommerce";
            String content = emailTemplate.generateWelcomeEmail(recipientName);
            
            EmailRequest request = EmailRequest.builder()
                .to(to)
                .subject(subject)
                .content(content)
                .recipientName(recipientName)
                .build();
            
            return sendHtmlEmail(request);
            
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", to, e);
            return new EmailResponse(false, "Failed to send welcome email: " + e.getMessage(), LocalDateTime.now());
        }
    }
}
