package com.importer_ecommerce.importEcommerce.cloudstorage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cloudflare.r2")
public class CloudflareR2Config {
    
    private String endpoint;
    private String accessKeyId;
    private String secretAccessKey;
    private String bucketName;
    private String region = "auto";
    private String publicUrl;
    
    // Optional configurations
    private int maxFileSize = 10 * 1024 * 1024; // 10MB default
    private String allowedImageTypes = "jpg,jpeg,png,gif,webp";
    private String allowedVideoTypes = "mp4,webm,mov,avi";
}
