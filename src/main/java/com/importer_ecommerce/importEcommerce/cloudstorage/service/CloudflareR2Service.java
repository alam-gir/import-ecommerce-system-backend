package com.importer_ecommerce.importEcommerce.cloudstorage.service;

import com.importer_ecommerce.importEcommerce.cloudstorage.dto.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CloudflareR2Service {
    
    /**
     * Upload a file to Cloudflare R2
     * @param file The file to upload
     * @param folder Optional folder name (e.g., "products", "categories", "profiles")
     * @return FileUploadResponse with file details and URL
     */
    FileUploadResponse uploadFile(MultipartFile file, String folder);
    
    /**
     * Upload a file to Cloudflare R2 (no folder)
     * @param file The file to upload
     * @return FileUploadResponse with file details and URL
     */
    FileUploadResponse uploadFile(MultipartFile file);
    
    /**
     * Delete a file from Cloudflare R2
     * @param fileUrl The full file URL to delete
     */
    void deleteFile(String fileUrl);
}
