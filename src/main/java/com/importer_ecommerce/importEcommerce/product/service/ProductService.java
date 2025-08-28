package com.importer_ecommerce.importEcommerce.product.service;

import com.importer_ecommerce.importEcommerce.product.dto.request.CreateProductRequest;
import com.importer_ecommerce.importEcommerce.product.dto.request.UpdateProductRequest;
import com.importer_ecommerce.importEcommerce.product.dto.response.ProductListResponse;
import com.importer_ecommerce.importEcommerce.product.dto.response.ProductResponse;
import static com.importer_ecommerce.importEcommerce.product.entity.Product.ProductStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    
    ProductResponse createProduct(CreateProductRequest request, List<MultipartFile> mediaFiles);
    
    ProductResponse updateProduct(UUID id, UpdateProductRequest request, List<MultipartFile> mediaFiles);
    
    ProductResponse getProductById(UUID id);
    
    ProductResponse getProductBySlug(String slug);
    
    ProductListResponse getAllProducts(Pageable pageable);
    
    ProductListResponse getProductsByStatus(ProductStatus status, Pageable pageable);
    
    ProductListResponse searchProducts(String query, Pageable pageable);
    
    ProductListResponse getProductsByCategory(UUID categoryId, Pageable pageable);
    
    List<ProductResponse> getFeaturedProducts(ProductStatus status);
    
    void deleteProduct(UUID id);
    
    void updateProductStatus(UUID id, ProductStatus status);
    
    // Media management methods
    ProductResponse addProductMedia(UUID productId, List<MultipartFile> mediaFiles);
    
    ProductResponse deleteProductMedia(UUID productId, List<String> mediaUrls);
}
