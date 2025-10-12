package com.importer_ecommerce.importEcommerce.cloudflare.service.impl;

import com.importer_ecommerce.importEcommerce.cloudflare.config.CloudflareConfig;
import com.importer_ecommerce.importEcommerce.cloudflare.service.CloudflareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

/**
 * Implementation of CloudflareService using AWS S3 SDK for R2
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CloudflareR2ServiceImpl implements CloudflareService {
    
    private final CloudflareConfig config;
    
    @Override
    public String uploadFile(MultipartFile file, String folderPath) {
        validateFile(file);
        
        String fileName = generateFileName(file);
        String key = buildKey(folderPath, fileName);
        
        try (S3Client s3Client = createS3Client()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(config.getBucketName())
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();
            
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(
                file.getInputStream(), file.getSize()));
            
            String publicUrl = buildPublicUrl(key);
            log.info("File uploaded successfully: {}", publicUrl);
            return publicUrl;
            
        } catch (IOException e) {
            log.error("Error reading file: {}", e.getMessage());
            throw new RuntimeException("Failed to read file", e);
        } catch (Exception e) {
            log.error("Error uploading file to R2: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file", e);
        }
    }
    
    @Override
    public String uploadFile(MultipartFile file) {
        return uploadFile(file, null);
    }
    
    @Override
    public boolean deleteFile(String publicUrl) {
        String key = extractKeyFromUrl(publicUrl);
        
        try (S3Client s3Client = createS3Client()) {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(config.getBucketName())
                .key(key)
                .build();
            
            s3Client.deleteObject(deleteRequest);
            log.info("File deleted successfully: {}", publicUrl);
            return true;
            
        } catch (Exception e) {
            log.error("Error deleting file from R2: {}", e.getMessage());
            return false;
        }
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        if (file.getSize() > config.getMaxFileSizeBytes()) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 50MB");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("File name is invalid");
        }
        
        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!isAllowedFileType(extension)) {
            throw new IllegalArgumentException("File type not allowed. Only images and videos are supported");
        }
    }
    
    private boolean isAllowedFileType(String extension) {
        return Arrays.asList(config.getAllowedImageExtensions()).contains(extension) ||
               Arrays.asList(config.getAllowedVideoExtensions()).contains(extension);
    }
    
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1) : "";
    }
    
    private String generateFileName(MultipartFile file) {
        String extension = getFileExtension(file.getOriginalFilename());
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("%s_%s.%s", timestamp, uuid, extension);
    }
    
    private String buildKey(String folderPath, String fileName) {
        if (folderPath == null || folderPath.trim().isEmpty()) {
            return fileName;
        }
        
        String cleanPath = folderPath.replaceAll("^/+|/+$", "");
        return cleanPath.isEmpty() ? fileName : cleanPath + "/" + fileName;
    }
    
    private String buildPublicUrl(String key) {
        return config.getPublicUrl() + "/" + key;
    }
    
    private String extractKeyFromUrl(String publicUrl) {
        String baseUrl = config.getPublicUrl();
        if (!publicUrl.startsWith(baseUrl)) {
            throw new IllegalArgumentException("Invalid public URL format");
        }
        return publicUrl.substring(baseUrl.length() + 1);
    }
    
    private S3Client createS3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
            config.getAccessKeyId(),
            config.getSecretAccessKey()
        );
        
        return S3Client.builder()
            .endpointOverride(URI.create(config.getEndpoint()))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.US_EAST_1)
            .build();
    }
}
