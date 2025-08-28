package com.importer_ecommerce.importEcommerce.cloudstorage.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String fileName;
    private String originalFileName;
    private String fileUrl;
    private String fileType; // IMAGE, VIDEO, DOCUMENT
    private Long fileSize;
    private String mimeType;
}
