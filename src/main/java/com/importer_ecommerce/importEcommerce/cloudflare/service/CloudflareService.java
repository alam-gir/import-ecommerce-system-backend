package com.importer_ecommerce.importEcommerce.cloudflare.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for Cloudflare R2 operations
 */
public interface CloudflareService {
    
    /**
     * Upload file to Cloudflare R2
     * @param file the file to upload
     * @param folderPath optional folder path (e.g., "products/2024/10/")
     * @return public URL of the uploaded file
     */
    String uploadFile(MultipartFile file, String folderPath);
    
    /**
     * Upload file to Cloudflare R2 root directory
     * @param file the file to upload
     * @return public URL of the uploaded file
     */
    String uploadFile(MultipartFile file);
    
    /**
     * Delete file from Cloudflare R2 using public URL
     * @param publicUrl the public URL of the file to delete
     * @return true if deletion successful, false otherwise
     */
    boolean deleteFile(String publicUrl);
}
