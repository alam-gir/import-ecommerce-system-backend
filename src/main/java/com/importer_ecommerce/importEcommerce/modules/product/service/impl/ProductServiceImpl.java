package com.importer_ecommerce.importEcommerce.modules.product.service.impl;

import com.importer_ecommerce.importEcommerce.cloudflare.service.CloudflareService;
import com.importer_ecommerce.importEcommerce.common.exception.ApiException;
import com.importer_ecommerce.importEcommerce.common.exception.ConflictException;
import com.importer_ecommerce.importEcommerce.common.exception.NotFoundException;
import com.importer_ecommerce.importEcommerce.modules.product.entity.Category;
import com.importer_ecommerce.importEcommerce.modules.product.entity.Product;
import com.importer_ecommerce.importEcommerce.modules.product.repository.CategoryRepository;
import com.importer_ecommerce.importEcommerce.modules.product.repository.ProductRepository;
import com.importer_ecommerce.importEcommerce.modules.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of ProductService
 * Handles all product-related business operations with proper error handling
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CloudflareService cloudflareService;
    
    @Override
    @Transactional
    public boolean createProduct(String title, String description, String categoryId,
                               Integer minimumOrderQuantity, MultipartFile profileImage,
                               List<MultipartFile> images, List<MultipartFile> descriptionImages) {
        try {
            // Validate category exists
            Category category = categoryRepository.findById(UUID.fromString(categoryId))
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + categoryId));
            
            // Check if product title already exists
            if (existsByTitle(title, null)) {
                throw new ConflictException("Product with title '" + title + "' already exists");
            }
            
            // Create product entity
            Product product = Product.builder()
                .title(title)
                .description(description)
                .category(category)
                .minimumOrderQuantity(minimumOrderQuantity)
                .images(new ArrayList<>())
                .descriptionImages(new ArrayList<>())
                .build();
            
            // Upload profile image if provided
            if (profileImage != null && !profileImage.isEmpty()) {
                String profileImageUrl = cloudflareService.uploadFile(profileImage, "products/" + product.getId() + "/images/");
                product.setProfileImage(profileImageUrl);
            }
            
            // Upload additional images if provided
            if (images != null && !images.isEmpty()) {
                List<String> imageUrls = new ArrayList<>();
                for (MultipartFile image : images) {
                    if (image != null && !image.isEmpty()) {
                        String imageUrl = cloudflareService.uploadFile(image, "products/" + product.getId() + "/images/");
                        imageUrls.add(imageUrl);
                    }
                }
                product.setImages(imageUrls);
            }
            
            // Upload description images if provided
            if (descriptionImages != null && !descriptionImages.isEmpty()) {
                List<String> descriptionImageUrls = new ArrayList<>();
                for (MultipartFile image : descriptionImages) {
                    if (image != null && !image.isEmpty()) {
                        String imageUrl = cloudflareService.uploadFile(image, "products/" + product.getId() + "/description/");
                        descriptionImageUrls.add(imageUrl);
                    }
                }
                product.setDescriptionImages(descriptionImageUrls);
            }
            
            // Save product
            productRepository.save(product);
            
            log.info("Product created successfully: {} with ID: {}", title, product.getId());
            return true;
            
        } catch (Exception e) {
            log.error("Failed to create product: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create product: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean updateProduct(UUID productId, String title, String description, String categoryId,
                                Integer minimumOrderQuantity, MultipartFile profileImage,
                                List<MultipartFile> images, List<MultipartFile> descriptionImages,
                                List<String> existingImages, List<String> existingDescriptionImages) {
        try {
            // Get existing product
            Product product = getProductById(productId);
            
            // Validate category exists
            Category category = categoryRepository.findById(UUID.fromString(categoryId))
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + categoryId));
            
            // Check if product title already exists (excluding current product)
            if (existsByTitle(title, productId)) {
                throw new ConflictException("Product with title '" + title + "' already exists");
            }
            
            // Update basic fields
            product.setTitle(title);
            product.setDescription(description);
            product.setCategory(category);
            product.setMinimumOrderQuantity(minimumOrderQuantity);
            
            // Handle profile image update
            if (profileImage != null && !profileImage.isEmpty()) {
                // Delete old profile image if exists
                if (product.getProfileImage() != null) {
                    cloudflareService.deleteFile(product.getProfileImage());
                }
                // Upload new profile image
                String profileImageUrl = cloudflareService.uploadFile(profileImage, "products/" + productId + "/images/");
                product.setProfileImage(profileImageUrl);
            }
            
            // Handle additional images update
            if (images != null && !images.isEmpty()) {
                // Delete old images that are not in existingImages list
                if (product.getImages() != null) {
                    for (String oldImageUrl : product.getImages()) {
                        if (existingImages == null || !existingImages.contains(oldImageUrl)) {
                            cloudflareService.deleteFile(oldImageUrl);
                        }
                    }
                }
                
                // Upload new images
                List<String> imageUrls = existingImages != null ? new ArrayList<>(existingImages) : new ArrayList<>();
                for (MultipartFile image : images) {
                    if (image != null && !image.isEmpty()) {
                        String imageUrl = cloudflareService.uploadFile(image, "products/" + productId + "/images/");
                        imageUrls.add(imageUrl);
                    }
                }
                product.setImages(imageUrls);
            }
            
            // Handle description images update
            if (descriptionImages != null && !descriptionImages.isEmpty()) {
                // Delete old description images that are not in existingDescriptionImages list
                if (product.getDescriptionImages() != null) {
                    for (String oldImageUrl : product.getDescriptionImages()) {
                        if (existingDescriptionImages == null || !existingDescriptionImages.contains(oldImageUrl)) {
                            cloudflareService.deleteFile(oldImageUrl);
                        }
                    }
                }
                
                // Upload new description images
                List<String> descriptionImageUrls = existingDescriptionImages != null ? new ArrayList<>(existingDescriptionImages) : new ArrayList<>();
                for (MultipartFile image : descriptionImages) {
                    if (image != null && !image.isEmpty()) {
                        String imageUrl = cloudflareService.uploadFile(image, "products/" + productId + "/description/");
                        descriptionImageUrls.add(imageUrl);
                    }
                }
                product.setDescriptionImages(descriptionImageUrls);
            }
            
            // Save updated product
            productRepository.save(product);
            
            log.info("Product updated successfully: {} with ID: {}", title, productId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to update product: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update product: " + e.getMessage());
        }
    }
    
    @Override
    public Product getProductById(UUID productId) {
        return productRepository.findByIdAndNotDeleted(productId)
            .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));
    }
    
    @Override
    public Product getProductBasicById(UUID productId) {
        return productRepository.findByIdAndNotDeleted(productId)
            .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));
    }
    
    @Override
    public Page<Product> getAllProducts(Pageable pageable, String search, String categoryId) {
        if (search != null && !search.trim().isEmpty() && categoryId != null && !categoryId.trim().isEmpty()) {
            // Search by title and filter by category
            return productRepository.findByTitleContainingIgnoreCaseAndCategoryId(search, UUID.fromString(categoryId), pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            // Search by title only
            return productRepository.findByTitleContainingIgnoreCase(search, pageable);
        } else if (categoryId != null && !categoryId.trim().isEmpty()) {
            // Filter by category only
            return productRepository.findByCategoryId(UUID.fromString(categoryId), pageable);
        } else {
            // Get all products
            return productRepository.findAllActive(pageable);
        }
    }
    
    @Override
    public List<Product> getProductsByCategory(UUID categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
    
    @Override
    @Transactional
    public boolean deleteProduct(UUID productId) {
        try {
            Product product = getProductById(productId);
            
            // Delete profile image
            if (product.getProfileImage() != null) {
                cloudflareService.deleteFile(product.getProfileImage());
            }
            
            // Delete additional images
            if (product.getImages() != null) {
                for (String imageUrl : product.getImages()) {
                    cloudflareService.deleteFile(imageUrl);
                }
            }
            
            // Delete description images
            if (product.getDescriptionImages() != null) {
                for (String imageUrl : product.getDescriptionImages()) {
                    cloudflareService.deleteFile(imageUrl);
                }
            }
            
            // Hard delete product
            productRepository.delete(product);
            
            log.info("Product deleted successfully: {} with ID: {}", product.getTitle(), productId);
            return true;
            
        } catch (NotFoundException e) {
            // Re-throw NotFoundException to be handled by controller
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete product: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete product: " + e.getMessage());
        }
    }
    
    @Override
    public boolean existsByTitle(String title, UUID excludeId) {
        return productRepository.existsByTitleAndNotDeleted(title, excludeId);
    }
    
    @Override
    public List<Product> getProductsWithLowStock() {
        return productRepository.findProductsWithLowStockVariants();
    }
    
    @Override
    public List<Product> getProductsWithoutVariants() {
        return productRepository.findProductsWithoutVariants();
    }
    
    @Override
    public List<Product> getProductsWithVariants() {
        return productRepository.findProductsWithVariants();
    }
    
    @Override
    public List<Product> getProductsByMinimumOrderQuantityRange(Integer minQuantity, Integer maxQuantity) {
        return productRepository.findByMinimumOrderQuantityBetween(minQuantity, maxQuantity);
    }
    
    @Override
    @Transactional
    public boolean updateProductCategory(UUID productId, UUID categoryId) {
        try {
            Product product = getProductById(productId);
            
            // Validate category exists
            Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + categoryId));
            
            product.setCategory(category);
            productRepository.save(product);
            
            log.info("Product category updated successfully: {} -> {}", product.getTitle(), category.getTitle());
            return true;
            
        } catch (NotFoundException e) {
            // Re-throw NotFoundException to be handled by controller
            throw e;
        } catch (Exception e) {
            log.error("Failed to update product category: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update product category: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean updateProductProfileImage(UUID productId, MultipartFile profileImage) {
        try {
            Product product = getProductById(productId);
            
            String oldImageUrl = product.getProfileImage();
            String newImageUrl = null;
            
            if (profileImage != null && !profileImage.isEmpty()) {
                newImageUrl = cloudflareService.uploadFile(profileImage, "products");
                log.info("New product profile image uploaded to Cloudflare: {}", newImageUrl);
                
                // Delete old image if it exists
                if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                    try {
                        cloudflareService.deleteFile(oldImageUrl);
                        log.info("Old product profile image deleted from Cloudflare: {}", oldImageUrl);
                    } catch (Exception e) {
                        log.warn("Failed to delete old product profile image: {}", e.getMessage());
                    }
                }
            }
            
            product.setProfileImage(newImageUrl);
            productRepository.save(product);
            
            log.info("Product profile image updated successfully: {}", product.getTitle());
            return true;
            
        } catch (NotFoundException e) {
            // Re-throw NotFoundException to be handled by controller
            throw e;
        } catch (Exception e) {
            log.error("Failed to update product profile image: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update product profile image: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean addProductImages(UUID productId, List<MultipartFile> images) {
        try {
            Product product = getProductById(productId);
            
            if (images != null && !images.isEmpty()) {
                List<String> newImageUrls = new ArrayList<>();
                
                for (MultipartFile image : images) {
                    if (image != null && !image.isEmpty()) {
                        String imageUrl = cloudflareService.uploadFile(image, "products");
                        newImageUrls.add(imageUrl);
                        log.info("New product image uploaded to Cloudflare: {}", imageUrl);
                    }
                }
                
                // Add new images to existing images
                List<String> existingImages = product.getImages() != null ? new ArrayList<>(product.getImages()) : new ArrayList<>();
                existingImages.addAll(newImageUrls);
                product.setImages(existingImages);
                
                productRepository.save(product);
                log.info("Product images added successfully: {} ({} new images)", product.getTitle(), newImageUrls.size());
            }
            
            return true;
            
        } catch (NotFoundException e) {
            // Re-throw NotFoundException to be handled by controller
            throw e;
        } catch (Exception e) {
            log.error("Failed to add product images: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add product images: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean removeProductImages(UUID productId, List<String> imageUrls) {
        try {
            Product product = getProductById(productId);
            
            if (imageUrls != null && !imageUrls.isEmpty() && product.getImages() != null) {
                List<String> existingImages = new ArrayList<>(product.getImages());
                
                for (String imageUrl : imageUrls) {
                    if (existingImages.contains(imageUrl)) {
                        // Delete from Cloudflare
                        try {
                            cloudflareService.deleteFile(imageUrl);
                            log.info("Product image deleted from Cloudflare: {}", imageUrl);
                        } catch (Exception e) {
                            log.warn("Failed to delete product image from Cloudflare: {}", e.getMessage());
                        }
                        
                        // Remove from list
                        existingImages.remove(imageUrl);
                    }
                }
                
                product.setImages(existingImages);
                productRepository.save(product);
                log.info("Product images removed successfully: {} ({} images removed)", product.getTitle(), imageUrls.size());
            }
            
            return true;
            
        } catch (NotFoundException e) {
            // Re-throw NotFoundException to be handled by controller
            throw e;
        } catch (Exception e) {
            log.error("Failed to remove product images: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to remove product images: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean addProductDescriptionImages(UUID productId, List<MultipartFile> descriptionImages) {
        try {
            Product product = getProductById(productId);
            
            if (descriptionImages != null && !descriptionImages.isEmpty()) {
                List<String> newDescriptionImageUrls = new ArrayList<>();
                
                for (MultipartFile image : descriptionImages) {
                    if (image != null && !image.isEmpty()) {
                        String imageUrl = cloudflareService.uploadFile(image, "products/description");
                        newDescriptionImageUrls.add(imageUrl);
                        log.info("New product description image uploaded to Cloudflare: {}", imageUrl);
                    }
                }
                
                // Add new description images to existing description images
                List<String> existingDescriptionImages = product.getDescriptionImages() != null ? new ArrayList<>(product.getDescriptionImages()) : new ArrayList<>();
                existingDescriptionImages.addAll(newDescriptionImageUrls);
                product.setDescriptionImages(existingDescriptionImages);
                
                productRepository.save(product);
                log.info("Product description images added successfully: {} ({} new images)", product.getTitle(), newDescriptionImageUrls.size());
            }
            
            return true;
            
        } catch (NotFoundException e) {
            // Re-throw NotFoundException to be handled by controller
            throw e;
        } catch (Exception e) {
            log.error("Failed to add product description images: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add product description images: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean removeProductDescriptionImages(UUID productId, List<String> descriptionImageUrls) {
        try {
            Product product = getProductById(productId);
            
            if (descriptionImageUrls != null && !descriptionImageUrls.isEmpty() && product.getDescriptionImages() != null) {
                List<String> existingDescriptionImages = new ArrayList<>(product.getDescriptionImages());
                
                for (String imageUrl : descriptionImageUrls) {
                    if (existingDescriptionImages.contains(imageUrl)) {
                        // Delete from Cloudflare
                        try {
                            cloudflareService.deleteFile(imageUrl);
                            log.info("Product description image deleted from Cloudflare: {}", imageUrl);
                        } catch (Exception e) {
                            log.warn("Failed to delete product description image from Cloudflare: {}", e.getMessage());
                        }
                        
                        // Remove from list
                        existingDescriptionImages.remove(imageUrl);
                    }
                }
                
                product.setDescriptionImages(existingDescriptionImages);
                productRepository.save(product);
                log.info("Product description images removed successfully: {} ({} images removed)", product.getTitle(), descriptionImageUrls.size());
            }
            
            return true;
            
        } catch (NotFoundException e) {
            // Re-throw NotFoundException to be handled by controller
            throw e;
        } catch (Exception e) {
            log.error("Failed to remove product description images: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to remove product description images: " + e.getMessage());
        }
    }
}

