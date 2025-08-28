package com.importer_ecommerce.importEcommerce.product.service.impl;

import com.importer_ecommerce.importEcommerce.cloudstorage.service.CloudflareR2Service;
import com.importer_ecommerce.importEcommerce.common.exception.ConflictException;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.product.dto.request.CreateProductVariantRequest;
import com.importer_ecommerce.importEcommerce.product.dto.request.UpdateProductVariantRequest;
import com.importer_ecommerce.importEcommerce.product.dto.response.ProductVariantListResponse;
import com.importer_ecommerce.importEcommerce.product.dto.response.ProductVariantResponse;
import com.importer_ecommerce.importEcommerce.product.entity.Product;
import com.importer_ecommerce.importEcommerce.product.entity.ProductVariant;
import com.importer_ecommerce.importEcommerce.product.mapper.ProductVariantMapper;
import com.importer_ecommerce.importEcommerce.product.repository.ProductRepository;
import com.importer_ecommerce.importEcommerce.product.repository.ProductVariantRepository;
import com.importer_ecommerce.importEcommerce.product.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductVariantServiceImpl implements ProductVariantService {
    
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final ProductVariantMapper productVariantMapper;
    private final CloudflareR2Service cloudflareR2Service;
    
    @Override
    public ProductVariantResponse createProductVariant(UUID productId, CreateProductVariantRequest request, List<MultipartFile> images) {
        // Check if product exists
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("Product", productId.toString()));
        
        // Check if SKU already exists
        if (productVariantRepository.existsBySku(request.getSku())) {
            throw new ConflictException("Product variant with SKU '" + request.getSku() + "' already exists");
        }
        
        ProductVariant variant = new ProductVariant();
        variant.setSku(request.getSku());
        variant.setTitle(request.getTitle());
        variant.setPrice(request.getPrice());
        variant.setCompareAtPrice(request.getCompareAtPrice());
        variant.setInventoryQuantity(request.getInventoryQuantity());
        variant.setInventoryTracking(request.getInventoryTracking());
        variant.setLowStockThreshold(request.getLowStockThreshold());
        variant.setAttributes(request.getAttributes());
        variant.setIsActive(request.getIsActive());
        variant.setProduct(product);
        
        // Handle images
        List<String> imageUrls = uploadMediaFiles(images, "product-variants");
        variant.setImages(imageUrls);
        
        ProductVariant savedVariant = productVariantRepository.save(variant);
        return productVariantMapper.toResponse(savedVariant);
    }
    
    @Override
    public ProductVariantResponse updateProductVariant(UUID id, UpdateProductVariantRequest request, List<MultipartFile> images) {
        ProductVariant variant = productVariantRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product Variant", id.toString()));
        
        // Check if SKU already exists for another variant
        if (!variant.getSku().equals(request.getSku()) && 
            productVariantRepository.existsBySkuAndIdNot(request.getSku(), id)) {
            throw new ConflictException("Product variant with SKU '" + request.getSku() + "' already exists");
        }
        
        variant.setSku(request.getSku());
        variant.setTitle(request.getTitle());
        
        if (request.getPrice() != null) {
            variant.setPrice(request.getPrice());
        }
        
        variant.setCompareAtPrice(request.getCompareAtPrice());
        
        if (request.getInventoryQuantity() != null) {
            variant.setInventoryQuantity(request.getInventoryQuantity());
        }
        
        if (request.getInventoryTracking() != null) {
            variant.setInventoryTracking(request.getInventoryTracking());
        }
        
        variant.setLowStockThreshold(request.getLowStockThreshold());
        variant.setAttributes(request.getAttributes());
        
        if (request.getIsActive() != null) {
            variant.setIsActive(request.getIsActive());
        }
        
        // Handle images if provided
        if (images != null && !images.isEmpty()) {
            List<String> imageUrls = uploadMediaFiles(images, "product-variants");
            variant.setImages(imageUrls);
        }
        
        ProductVariant updatedVariant = productVariantRepository.save(variant);
        return productVariantMapper.toResponse(updatedVariant);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductVariantResponse getProductVariantById(UUID id) {
        ProductVariant variant = productVariantRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product Variant", id.toString()));
        return productVariantMapper.toResponse(variant);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductVariantResponse getProductVariantBySku(String sku) {
        ProductVariant variant = productVariantRepository.findBySku(sku)
            .orElseThrow(() -> new NotFoundException("Product Variant", sku));
        return productVariantMapper.toResponse(variant);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductVariantListResponse getProductVariantsByProduct(UUID productId, Pageable pageable) {
        Page<ProductVariant> variantPage = productVariantRepository.findByProductId(productId, pageable);
        List<ProductVariantResponse> variants = variantPage.getContent().stream()
            .map(productVariantMapper::toResponse)
            .collect(Collectors.toList());
        
        return new ProductVariantListResponse(
            variants,
            variantPage.getTotalElements(),
            variantPage.getTotalPages(),
            variantPage.getNumber(),
            variantPage.getSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductVariantListResponse getAllProductVariants(Pageable pageable) {
        Page<ProductVariant> variantPage = productVariantRepository.findAll(pageable);
        List<ProductVariantResponse> variants = variantPage.getContent().stream()
            .map(productVariantMapper::toResponse)
            .collect(Collectors.toList());
        
        return new ProductVariantListResponse(
            variants,
            variantPage.getTotalElements(),
            variantPage.getTotalPages(),
            variantPage.getNumber(),
            variantPage.getSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductVariantListResponse searchProductVariants(String query, Pageable pageable) {
        Page<ProductVariant> variantPage = productVariantRepository.searchVariants(query, pageable);
        List<ProductVariantResponse> variants = variantPage.getContent().stream()
            .map(productVariantMapper::toResponse)
            .collect(Collectors.toList());
        
        return new ProductVariantListResponse(
            variants,
            variantPage.getTotalElements(),
            variantPage.getTotalPages(),
            variantPage.getNumber(),
            variantPage.getSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductVariantListResponse getLowStockVariants(Pageable pageable) {
        Page<ProductVariant> variantPage = productVariantRepository.findLowStockVariants(pageable);
        List<ProductVariantResponse> variants = variantPage.getContent().stream()
            .map(productVariantMapper::toResponse)
            .collect(Collectors.toList());
        
        return new ProductVariantListResponse(
            variants,
            variantPage.getTotalElements(),
            variantPage.getTotalPages(),
            variantPage.getNumber(),
            variantPage.getSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductVariantListResponse getOutOfStockVariants(Pageable pageable) {
        Page<ProductVariant> variantPage = productVariantRepository.findOutOfStockVariants(pageable);
        List<ProductVariantResponse> variants = variantPage.getContent().stream()
            .map(productVariantMapper::toResponse)
            .collect(Collectors.toList());
        
        return new ProductVariantListResponse(
            variants,
            variantPage.getTotalElements(),
            variantPage.getTotalPages(),
            variantPage.getNumber(),
            variantPage.getSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantResponse> getActiveVariantsByProduct(UUID productId) {
        List<ProductVariant> variants = productVariantRepository.findActiveByProductId(productId);
        return variants.stream()
            .map(productVariantMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteProductVariant(UUID id) {
        if (!productVariantRepository.existsById(id)) {
            throw new NotFoundException("Product Variant", id.toString());
        }
        productVariantRepository.deleteById(id);
    }
    
    @Override
    public void updateInventoryQuantity(UUID id, Integer quantity) {
        ProductVariant variant = productVariantRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product Variant", id.toString()));
        
        if (quantity < 0) {
            throw new IllegalArgumentException("Inventory quantity cannot be negative");
        }
        
        variant.setInventoryQuantity(quantity);
        productVariantRepository.save(variant);
    }
    
    @Override
    public void updateVariantStatus(UUID id, Boolean isActive) {
        ProductVariant variant = productVariantRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product Variant", id.toString()));
        
        variant.setIsActive(isActive);
        productVariantRepository.save(variant);
    }
    
    @Override
    public ProductVariantResponse addVariantMedia(UUID variantId, List<MultipartFile> mediaFiles) {
        ProductVariant variant = productVariantRepository.findById(variantId)
            .orElseThrow(() -> new NotFoundException("Product Variant", variantId.toString()));
        
        List<String> newImageUrls = uploadMediaFiles(mediaFiles, "product-variants");
        
        List<String> currentImages = variant.getImages();
        if (currentImages == null) {
            currentImages = new ArrayList<>();
        }
        currentImages.addAll(newImageUrls);
        
        variant.setImages(currentImages);
        ProductVariant savedVariant = productVariantRepository.save(variant);
        return productVariantMapper.toResponse(savedVariant);
    }
    
    @Override
    public ProductVariantResponse deleteVariantMedia(UUID variantId, List<String> mediaUrls) {
        ProductVariant variant = productVariantRepository.findById(variantId)
            .orElseThrow(() -> new NotFoundException("Product Variant", variantId.toString()));
        
        List<String> currentImages = variant.getImages();
        if (currentImages != null) {
            // Remove the specified URLs
            currentImages.removeAll(mediaUrls);
            
            // Delete files from cloud storage
            for (String mediaUrl : mediaUrls) {
                try {
                    cloudflareR2Service.deleteFile(mediaUrl);
                } catch (Exception e) {
                    log.error("Failed to delete media file from cloud storage: {}", mediaUrl, e);
                }
            }
            
            variant.setImages(currentImages);
            ProductVariant savedVariant = productVariantRepository.save(variant);
            return productVariantMapper.toResponse(savedVariant);
        }
        
        return productVariantMapper.toResponse(variant);
    }
    
    private List<String> uploadMediaFiles(List<MultipartFile> mediaFiles, String folder) {
        List<String> mediaUrls = new ArrayList<>();
        
        if (mediaFiles != null && !mediaFiles.isEmpty()) {
            for (MultipartFile file : mediaFiles) {
                if (file != null && !file.isEmpty()) {
                    try {
                        var uploadResponse = cloudflareR2Service.uploadFile(file, folder);
                        mediaUrls.add(uploadResponse.getFileUrl());
                    } catch (Exception e) {
                        log.error("Failed to upload media file: {}", file.getOriginalFilename(), e);
                        throw new RuntimeException("Failed to upload media file: " + file.getOriginalFilename());
                    }
                }
            }
        }
        
        return mediaUrls;
    }
}