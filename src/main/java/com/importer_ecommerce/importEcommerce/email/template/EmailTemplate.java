package com.importer_ecommerce.importEcommerce.email.template;

import org.springframework.stereotype.Component;

/**
 * Email template service for generating HTML email templates
 */
@Component
public class EmailTemplate {
    
    private static final String COMPANY_NAME = "Taqreem Ecommerce";
    private static final String COMPANY_EMAIL = "info@taqreem.com";
    private static final String COMPANY_WEBSITE = "https://taqreem.com";
    
    /**
     * Generate OTP email template
     */
    public String generateOtpEmail(String recipientName, String otp) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>OTP Verification</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .otp-box { background: white; border: 2px dashed #667eea; padding: 20px; text-align: center; margin: 20px 0; border-radius: 8px; }
                    .otp-code { font-size: 32px; font-weight: bold; color: #667eea; letter-spacing: 5px; }
                    .footer { text-align: center; margin-top: 30px; color: #666; font-size: 14px; }
                    .button { display: inline-block; background: #667eea; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>{COMPANY_NAME}</h1>
                        <p>Your OTP Verification Code</p>
                    </div>
                    <div class="content">
                        <h2>Hello {RECIPIENT_NAME}!</h2>
                        <p>You have requested an OTP to access your account. Please use the following code to complete your verification:</p>
                        
                        <div class="otp-box">
                            <div class="otp-code">{OTP_CODE}</div>
                            <p><strong>This code will expire in 5 minutes</strong></p>
                        </div>
                        
                        <p>If you didn't request this code, please ignore this email or contact our support team.</p>
                        
                        <p>For security reasons, please do not share this code with anyone.</p>
                        
                        <div class="footer">
                            <p>Best regards,<br>The {COMPANY_NAME} Team</p>
                            <p>Email: {COMPANY_EMAIL} | Website: {COMPANY_WEBSITE}</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """.replace("{COMPANY_NAME}", COMPANY_NAME)
                .replace("{RECIPIENT_NAME}", recipientName)
                .replace("{OTP_CODE}", otp)
                .replace("{COMPANY_EMAIL}", COMPANY_EMAIL)
                .replace("{COMPANY_WEBSITE}", COMPANY_WEBSITE);
    }
    
    /**
     * Generate welcome email template
     */
    public String generateWelcomeEmail(String recipientName) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Welcome to {COMPANY_NAME}</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .welcome-box { background: white; padding: 20px; margin: 20px 0; border-radius: 8px; border-left: 4px solid #667eea; }
                    .footer { text-align: center; margin-top: 30px; color: #666; font-size: 14px; }
                    .button { display: inline-block; background: #667eea; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome to {COMPANY_NAME}!</h1>
                        <p>Your account has been successfully created</p>
                    </div>
                    <div class="content">
                        <h2>Hello {RECIPIENT_NAME}!</h2>
                        
                        <div class="welcome-box">
                            <p>Welcome to {COMPANY_NAME}! We're excited to have you on board.</p>
                            <p>Your account has been successfully created and you can now start exploring our platform.</p>
                        </div>
                        
                        <p>Here's what you can do next:</p>
                        <ul>
                            <li>Complete your profile setup</li>
                            <li>Explore our features</li>
                            <li>Contact our support team if you need any help</li>
                        </ul>
                        
                        <p>If you have any questions or need assistance, please don't hesitate to contact us.</p>
                        
                        <div class="footer">
                            <p>Best regards,<br>The {COMPANY_NAME} Team</p>
                            <p>Email: {COMPANY_EMAIL} | Website: {COMPANY_WEBSITE}</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """.replace("{COMPANY_NAME}", COMPANY_NAME)
                .replace("{RECIPIENT_NAME}", recipientName)
                .replace("{COMPANY_EMAIL}", COMPANY_EMAIL)
                .replace("{COMPANY_WEBSITE}", COMPANY_WEBSITE);
    }
    
    /**
     * Generate password reset email template
     */
    public String generatePasswordResetEmail(String recipientName, String resetLink) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Password Reset</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .reset-box { background: white; padding: 20px; margin: 20px 0; border-radius: 8px; border-left: 4px solid #667eea; }
                    .footer { text-align: center; margin-top: 30px; color: #666; font-size: 14px; }
                    .button { display: inline-block; background: #667eea; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>{COMPANY_NAME}</h1>
                        <p>Password Reset Request</p>
                    </div>
                    <div class="content">
                        <h2>Hello {RECIPIENT_NAME}!</h2>
                        
                        <div class="reset-box">
                            <p>We received a request to reset your password for your {COMPANY_NAME} account.</p>
                            <p>Click the button below to reset your password:</p>
                            <a href="{RESET_LINK}" class="button">Reset Password</a>
                            <p><strong>This link will expire in 10 minutes</strong></p>
                        </div>
                        
                        <p>If you didn't request this password reset, please ignore this email or contact our support team.</p>
                        
                        <div class="footer">
                            <p>Best regards,<br>The {COMPANY_NAME} Team</p>
                            <p>Email: {COMPANY_EMAIL} | Website: {COMPANY_WEBSITE}</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """.replace("{COMPANY_NAME}", COMPANY_NAME)
                .replace("{RECIPIENT_NAME}", recipientName)
                .replace("{RESET_LINK}", resetLink)
                .replace("{COMPANY_EMAIL}", COMPANY_EMAIL)
                .replace("{COMPANY_WEBSITE}", COMPANY_WEBSITE);
    }
}
