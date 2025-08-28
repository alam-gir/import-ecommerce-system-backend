package com.importer_ecommerce.importEcommerce.product.service.impl;

import com.importer_ecommerce.importEcommerce.cloudstorage.service.CloudflareR2Service;
import com.importer_ecommerce.importEcommerce.common.exception.ConflictException;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.product.dto.request.CreateProductRequest;
import com.importer_ecommerce.importEcommerce.product.dto.request.UpdateProductRequest;
import com.importer_ecommerce.importEcommerce.product.dto.response.ProductListResponse;
import com.importer_ecommerce.importEcommerce.product.dto.response.ProductResponse;
import com.importer_ecommerce.importEcommerce.product.entity.Category;
import com.importer_ecommerce.importEcommerce.product.entity.Product;
import static com.importer_ecommerce.importEcommerce.product.entity.Product.ProductStatus;
import com.importer_ecommerce.importEcommerce.product.mapper.ProductMapper;
import com.importer_ecommerce.importEcommerce.product.repository.CategoryRepository;
import com.importer_ecommerce.importEcommerce.product.repository.ProductRepository;
import com.importer_ecommerce.importEcommerce.product.service.ProductService;
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
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final CloudflareR2Service cloudflareR2Service;
    
    @Override
    public ProductResponse createProduct(CreateProductRequest request, List<MultipartFile> mediaFiles) {
        if (productRepository.existsBySlug(request.getSlug())) {
            throw new ConflictException("Product with slug '" + request.getSlug() + "' already exists");
        }
        
        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setSlug(request.getSlug());
        product.setDescription(request.getDescription());
        product.setNote(request.getNote());
        product.setBrand(request.getBrand());
        product.setAttributes(request.getAttributes());
        product.setStatus(request.getStatus());
        product.setFeatured(request.getFeatured());
        product.setSeoTitle(request.getSeoTitle());
        product.setSeoDescription(request.getSeoDescription());
        product.setSeoKeywords(request.getSeoKeywords());
        
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            product.setCategories(categories);
        }
        
        List<String> mediaUrls = uploadMediaFiles(mediaFiles, "products");
        product.setMediaDescriptions(mediaUrls);
        
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }
    
    @Override
    public ProductResponse updateProduct(UUID id, UpdateProductRequest request, List<MultipartFile> mediaFiles) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product", id.toString()));
        
        if (!product.getSlug().equals(request.getSlug()) && 
            productRepository.existsBySlugAndIdNot(request.getSlug(), id)) {
            throw new ConflictException("Product with slug '" + request.getSlug() + "' already exists");
        }
        
        product.setTitle(request.getTitle());
        product.setSlug(request.getSlug());
        product.setDescription(request.getDescription());
        product.setNote(request.getNote());
        product.setBrand(request.getBrand());
        product.setAttributes(request.getAttributes());
        
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        
        if (request.getFeatured() != null) {
            product.setFeatured(request.getFeatured());
        }
        
        product.setSeoTitle(request.getSeoTitle());
        product.setSeoDescription(request.getSeoDescription());
        product.setSeoKeywords(request.getSeoKeywords());
        
        if (request.getCategoryIds() != null) {
            if (request.getCategoryIds().isEmpty()) {
                product.setCategories(new ArrayList<>());
            } else {
                List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
                product.setCategories(categories);
            }
        }
        
        if (mediaFiles != null && !mediaFiles.isEmpty()) {
            List<String> mediaUrls = uploadMediaFiles(mediaFiles, "products");
            product.setMediaDescriptions(mediaUrls);
        }
        
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product", id.toString()));
        return productMapper.toResponse(product);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException("Product", slug));
        return productMapper.toResponse(product);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductListResponse getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        List<ProductResponse> products = productPage.getContent().stream()
            .map(productMapper::toResponse)
            .collect(Collectors.toList());
        
        return new ProductListResponse(
            products,
            productPage.getTotalElements(),
            productPage.getTotalPages(),
            productPage.getNumber(),
            productPage.getSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductListResponse getProductsByStatus(ProductStatus status, Pageable pageable) {
        Page<Product> productPage = productRepository.findByStatus(status, pageable);
        List<ProductResponse> products = productPage.getContent().stream()
            .map(productMapper::toResponse)
            .collect(Collectors.toList());
        
        return new ProductListResponse(
            products,
            productPage.getTotalElements(),
            productPage.getTotalPages(),
            productPage.getNumber(),
            productPage.getSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductListResponse searchProducts(String query, Pageable pageable) {
        Page<Product> productPage = productRepository.searchProducts(query, pageable);
        List<ProductResponse> products = productPage.getContent().stream()
            .map(productMapper::toResponse)
            .collect(Collectors.toList());
        
        return new ProductListResponse(
            products,
            productPage.getTotalElements(),
            productPage.getTotalPages(),
            productPage.getNumber(),
            productPage.getSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductListResponse getProductsByCategory(UUID categoryId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);
        List<ProductResponse> products = productPage.getContent().stream()
            .map(productMapper::toResponse)
            .collect(Collectors.toList());
        
        return new ProductListResponse(
            products,
            productPage.getTotalElements(),
            productPage.getTotalPages(),
            productPage.getNumber(),
            productPage.getSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getFeaturedProducts(ProductStatus status) {
        List<Product> featuredProducts = productRepository.findFeaturedByStatus(status);
        return featuredProducts.stream()
            .map(productMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException("Product", id.toString());
        }
        productRepository.deleteById(id);
    }
    
    @Override
    public void updateProductStatus(UUID id, ProductStatus status) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product", id.toString()));
        product.setStatus(status);
        productRepository.save(product);
    }
    
    @Override
    public ProductResponse addProductMedia(UUID productId, List<MultipartFile> mediaFiles) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("Product", productId.toString()));
        
        List<String> newMediaUrls = uploadMediaFiles(mediaFiles, "products");
        
        List<String> currentMedia = product.getMediaDescriptions();
        if (currentMedia == null) {
            currentMedia = new ArrayList<>();
        }
        currentMedia.addAll(newMediaUrls);
        
        product.setMediaDescriptions(currentMedia);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }
    
    @Override
    public ProductResponse deleteProductMedia(UUID productId, List<String> mediaUrls) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("Product", productId.toString()));
        
        List<String> currentMedia = product.getMediaDescriptions();
        if (currentMedia != null) {
            // Remove the specified URLs
            currentMedia.removeAll(mediaUrls);
            
            // Delete files from cloud storage
            for (String mediaUrl : mediaUrls) {
                try {
                    cloudflareR2Service.deleteFile(mediaUrl);
                } catch (Exception e) {
                    log.error("Failed to delete media file from cloud storage: {}", mediaUrl, e);
                }
            }
            
            product.setMediaDescriptions(currentMedia);
            Product savedProduct = productRepository.save(product);
            return productMapper.toResponse(savedProduct);
        }
        
        return productMapper.toResponse(product);
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
