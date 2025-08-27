package com.importer_ecommerce.importEcommerce.email.service;

import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {
    
    public String generateWelcomeEmail(String customerName) {
        return "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>Welcome to ImportEcommerce</title>" +
                "<style>" +
                    "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                    ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                    ".header { background: #007bff; color: white; padding: 20px; text-align: center; }" +
                    ".content { padding: 20px; background: #f9f9f9; }" +
                    ".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }" +
                "</style>" +
            "</head>" +
            "<body>" +
                "<div class=\"container\">" +
                    "<div class=\"header\">" +
                        "<h1>Welcome to ImportEcommerce!</h1>" +
                    "</div>" +
                    "<div class=\"content\">" +
                        "<h2>Hello " + customerName + "!</h2>" +
                        "<p>Thank you for joining ImportEcommerce. We're excited to have you as part of our community!</p>" +
                        "<p>You can now:</p>" +
                        "<ul>" +
                            "<li>Browse our product catalog</li>" +
                            "<li>Place orders</li>" +
                            "<li>Track your shipments</li>" +
                            "<li>Manage your profile</li>" +
                        "</ul>" +
                        "<p>If you have any questions, feel free to contact our support team.</p>" +
                    "</div>" +
                    "<div class=\"footer\">" +
                        "<p>© 2024 ImportEcommerce. All rights reserved.</p>" +
                    "</div>" +
                "</div>" +
            "</body>" +
            "</html>";
    }
    
    public String generateOtpEmail(String otp, String purpose) {
        String purposeText;
        switch (purpose) {
            case "EMAIL_VERIFICATION":
                purposeText = "email verification";
                break;
            case "PASSWORD_RESET":
                purposeText = "password reset";
                break;
            default:
                purposeText = "verification";
                break;
        }
        
        return "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>Your Verification Code</title>" +
                "<style>" +
                    "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                    ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                    ".header { background: #28a745; color: white; padding: 20px; text-align: center; }" +
                    ".content { padding: 20px; background: #f9f9f9; }" +
                    ".otp-box { background: #fff; border: 2px solid #28a745; padding: 20px; text-align: center; margin: 20px 0; }" +
                    ".otp-code { font-size: 32px; font-weight: bold; color: #28a745; letter-spacing: 5px; }" +
                    ".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }" +
                "</style>" +
            "</head>" +
            "<body>" +
                "<div class=\"container\">" +
                    "<div class=\"header\">" +
                        "<h1>Your Verification Code</h1>" +
                    "</div>" +
                    "<div class=\"content\">" +
                        "<p>You requested a verification code for " + purposeText + ".</p>" +
                        "<p>Here's your verification code:</p>" +
                        "<div class=\"otp-box\">" +
                            "<div class=\"otp-code\">" + otp + "</div>" +
                        "</div>" +
                        "<p><strong>Important:</strong></p>" +
                        "<ul>" +
                            "<li>This code will expire in 10 minutes</li>" +
                            "<li>Never share this code with anyone</li>" +
                            "<li>If you didn't request this code, please ignore this email</li>" +
                        "</ul>" +
                    "</div>" +
                    "<div class=\"footer\">" +
                        "<p>© 2024 ImportEcommerce. All rights reserved.</p>" +
                    "</div>" +
                "</div>" +
            "</body>" +
            "</html>";
    }
    
    public String generateProfileUpdateEmail(String customerName, String updatedField) {
        return "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>Profile Updated</title>" +
                "<style>" +
                    "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                    ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                    ".header { background: #17a2b8; color: white; padding: 20px; text-align: center; }" +
                    ".content { padding: 20px; background: #f9f9f9; }" +
                    ".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }" +
                "</style>" +
            "</head>" +
            "<body>" +
                "<div class=\"container\">" +
                    "<div class=\"header\">" +
                        "<h1>Profile Updated Successfully</h1>" +
                    "</div>" +
                    "<div class=\"content\">" +
                        "<h2>Hello " + customerName + "!</h2>" +
                        "<p>Your profile has been updated successfully.</p>" +
                        "<p><strong>Updated field:</strong> " + updatedField + "</p>" +
                        "<p>If you didn't make this change, please contact our support team immediately.</p>" +
                    "</div>" +
                    "<div class=\"footer\">" +
                        "<p>© 2024 ImportEcommerce. All rights reserved.</p>" +
                    "</div>" +
                "</div>" +
            "</body>" +
            "</html>";
    }
}
