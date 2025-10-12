package com.importer_ecommerce.importEcommerce.cloudflare.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Cloudflare R2
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "cloudflare.r2")
public class CloudflareConfig {
    
    private String bucketName;
    private String endpoint;
    private String accessKeyId;
    private String secretAccessKey;
    private String publicUrl;
    
    // File validation settings
    private long maxFileSizeBytes = 50 * 1024 * 1024; // 50MB default
    
    // Allowed file extensions
    private String[] allowedImageExtensions = {
        "jpg", "jpeg", "png", "gif", "webp", "bmp", "svg", "tiff"
    };
    
    private String[] allowedVideoExtensions = {
        "mp4", "avi", "mov", "wmv", "flv", "webm", "mkv", "m4v"
    };
}
