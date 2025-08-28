package com.importer_ecommerce.importEcommerce.product.service;

import com.importer_ecommerce.importEcommerce.product.dto.request.CreateProductVariantRequest;
import com.importer_ecommerce.importEcommerce.product.dto.request.UpdateProductVariantRequest;
import com.importer_ecommerce.importEcommerce.product.dto.response.ProductVariantListResponse;
import com.importer_ecommerce.importEcommerce.product.dto.response.ProductVariantResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProductVariantService {
    
    ProductVariantResponse createProductVariant(UUID productId, CreateProductVariantRequest request, List<MultipartFile> images);
    
    ProductVariantResponse updateProductVariant(UUID id, UpdateProductVariantRequest request, List<MultipartFile> images);
    
    ProductVariantResponse getProductVariantById(UUID id);
    
    ProductVariantResponse getProductVariantBySku(String sku);
    
    ProductVariantListResponse getProductVariantsByProduct(UUID productId, Pageable pageable);
    
    ProductVariantListResponse getAllProductVariants(Pageable pageable);
    
    ProductVariantListResponse searchProductVariants(String query, Pageable pageable);
    
    ProductVariantListResponse getLowStockVariants(Pageable pageable);
    
    ProductVariantListResponse getOutOfStockVariants(Pageable pageable);
    
    List<ProductVariantResponse> getActiveVariantsByProduct(UUID productId);
    
    void deleteProductVariant(UUID id);
    
    void updateInventoryQuantity(UUID id, Integer quantity);
    
    void updateVariantStatus(UUID id, Boolean isActive);
    
    // Media management methods
    ProductVariantResponse addVariantMedia(UUID variantId, List<MultipartFile> mediaFiles);
    
    ProductVariantResponse deleteVariantMedia(UUID variantId, List<String> mediaUrls);
}
