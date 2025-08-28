package com.importer_ecommerce.importEcommerce.product.repository;

import com.importer_ecommerce.importEcommerce.product.entity.Product;
import static com.importer_ecommerce.importEcommerce.product.entity.Product.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    
    Optional<Product> findBySlug(String slug);
    
    boolean existsBySlug(String slug);
    
    boolean existsBySlugAndIdNot(String slug, UUID id);
    
    @Query("SELECT p FROM Product p WHERE p.status = :status")
    Page<Product> findByStatus(@Param("status") ProductStatus status, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.featured = true AND p.status = :status")
    List<Product> findFeaturedByStatus(@Param("status") ProductStatus status);
    
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Product> searchProducts(@Param("query") String query, Pageable pageable);
    
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id = :categoryId")
    Page<Product> findByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);
    
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id IN :categoryIds")
    Page<Product> findByCategoryIds(@Param("categoryIds") List<UUID> categoryIds, Pageable pageable);
}
