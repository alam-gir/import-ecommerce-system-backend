package com.importer_ecommerce.importEcommerce.cloudstorage.service.impl;

import com.importer_ecommerce.importEcommerce.cloudstorage.config.CloudflareR2Config;
import com.importer_ecommerce.importEcommerce.cloudstorage.dto.FileUploadResponse;
import com.importer_ecommerce.importEcommerce.cloudstorage.service.CloudflareR2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudflareR2ServiceImpl implements CloudflareR2Service {
    
    private final CloudflareR2Config config;
    private final S3Client s3Client;
    
    @Override
    public FileUploadResponse uploadFile(MultipartFile file, String folder) {
        try {
            // Validate file
            validateFile(file);
            
            // Generate unique filename
            String originalFileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFileName);
            String fileName = generateUniqueFileName(fileExtension);
            String bucketPath = folder + "/" + fileName;
            
            // Upload to R2
            uploadToR2(bucketPath, file);
            
            log.info("File uploaded successfully: {} to {}", originalFileName, bucketPath);
            
            return FileUploadResponse.builder()
                    .fileName(fileName)
                    .originalFileName(originalFileName)
                    .fileUrl(getFileUrl(bucketPath))
                    .fileType(determineFileType(file.getContentType()))
                    .fileSize(file.getSize())
                    .mimeType(file.getContentType())
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }
    
    @Override
    public FileUploadResponse uploadFile(MultipartFile file) {
        return uploadFile(file, "general");
    }
    
    @Override
    public void deleteFile(String fileUrl) {
        try {
            String bucketPath = extractBucketPathFromUrl(fileUrl);
            
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(config.getBucketName())
                    .key(bucketPath)
                    .build();
            
            s3Client.deleteObject(deleteRequest);
            
            log.info("File deleted successfully: {}", bucketPath);
            
        } catch (Exception e) {
            log.error("Failed to delete file: {}", fileUrl, e);
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        
        if (file.getSize() > config.getMaxFileSize()) {
            throw new RuntimeException("File size exceeds limit");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedFileType(contentType)) {
            throw new RuntimeException("File type not allowed");
        }
    }
    
    private boolean isAllowedFileType(String contentType) {
        // Check image types
        if (contentType.startsWith("image/")) {
            String[] allowedTypes = config.getAllowedImageTypes().split(",");
            for (String type : allowedTypes) {
                if (contentType.equals("image/" + type.trim())) {
                    return true;
                }
            }
        }
        
        // Check video types
        if (contentType.startsWith("video/")) {
            String[] allowedVideoTypes = config.getAllowedVideoTypes().split(",");
            for (String type : allowedVideoTypes) {
                if (contentType.equals("video/" + type.trim())) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
    
    private String generateUniqueFileName(String extension) {
        return UUID.randomUUID().toString() + extension;
    }
    
    private void uploadToR2(String bucketPath, MultipartFile file) throws IOException {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(config.getBucketName())
                .key(bucketPath)
                .contentType(file.getContentType())
                .build();
        
        s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }
    
    private String getFileUrl(String bucketPath) {
        return config.getPublicUrl() + "/" + bucketPath;
    }
    
    private String extractBucketPathFromUrl(String fileUrl) {
        // Remove the base URL to get just the path
        // Example: https://cdn.example.com/products/image.jpg -> products/image.jpg
        if (fileUrl.contains("/")) {
            return fileUrl.substring(fileUrl.indexOf("/", 8)); // Skip https://
        }
        return fileUrl;
    }
    
    private String determineFileType(String contentType) {
        if (contentType.startsWith("image/")) {
            return "IMAGE";
        } else if (contentType.startsWith("video/")) {
            return "VIDEO";
        } else {
            return "DOCUMENT";
        }
    }
}

