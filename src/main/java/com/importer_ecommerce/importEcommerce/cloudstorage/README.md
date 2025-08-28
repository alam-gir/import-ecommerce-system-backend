# Simple Cloud Storage Service

A lightweight, reusable cloud storage service using Cloudflare R2 (S3-compatible storage).

## Features

- **Simple File Upload**: Upload files with optional folder organization
- **File Validation**: Type and size validation
- **UUID Naming**: Unique file names to prevent conflicts
- **No Database**: Simple file storage without metadata persistence

## Usage

### 1. Inject the service into your other services:

```java
@Service
public class ProductService {
    
    private final CloudflareR2Service storageService;
    
    public ProductService(CloudflareR2Service storageService) {
        this.storageService = storageService;
    }
    
    public Product createProduct(CreateProductRequest request, MultipartFile image) {
        // Upload image to products folder
        FileUploadResponse fileResponse = storageService.uploadFile(image, "products");
        
        // Create product with image URL
        Product product = new Product();
        product.setName(request.getName());
        product.setImageUrl(fileResponse.getFileUrl());
        // ... other fields
        
        return productRepository.save(product);
    }
}
```

### 2. Upload without folder (goes to "general" folder):

```java
FileUploadResponse response = storageService.uploadFile(file);
String imageUrl = response.getFileUrl();
```

### 3. Upload to specific folder:

```java
FileUploadResponse response = storageService.uploadFile(file, "categories");
String categoryImageUrl = response.getFileUrl();
```

### 4. Delete a file:

```java
storageService.deleteFile("https://cdn.example.com/products/image.jpg");
```

## Configuration

Add to `application.properties`:

```properties
cloudflare.r2.endpoint=https://your-account-id.r2.cloudflarestorage.com
cloudflare.r2.access-key-id=your-access-key-id
cloudflare.r2.secret-access-key=your-secret-access-key
cloudflare.r2.bucket-name=your-bucket-name
cloudflare.r2.public-url=https://your-public-domain.com
cloudflare.r2.max-file-size=10485760
cloudflare.r2.allowed-image-types=jpg,jpeg,png,gif,webp
cloudflare.r2.allowed-video-types=mp4,webm,mov,avi
```

## Response Object

```java
FileUploadResponse {
    fileName: "uuid-generated-name.jpg",
    originalFileName: "product-image.jpg", 
    fileUrl: "https://cdn.example.com/products/uuid-generated-name.jpg",
    fileType: "IMAGE",
    fileSize: 1024000,
    mimeType: "image/jpeg"
}
```

## Folder Structure

- `products/` - Product images and videos
- `categories/` - Category images  
- `profiles/` - User profile pictures
- `general/` - Default folder for uploads without folder specification
