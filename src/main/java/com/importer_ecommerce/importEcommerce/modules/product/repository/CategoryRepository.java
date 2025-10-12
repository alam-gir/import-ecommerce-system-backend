package com.importer_ecommerce.importEcommerce.modules.product.repository;

import com.importer_ecommerce.importEcommerce.modules.product.entity.Category;
import com.importer_ecommerce.importEcommerce.modules.product.entity.CategoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Category entity
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    
    /**
     * Find category by title (case insensitive)
     */
    Optional<Category> findByTitleIgnoreCase(String title);
    
    /**
     * Find categories by title containing (case insensitive)
     */
    List<Category> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Find all active categories (not deleted)
     */
    @Query("SELECT c FROM Category c WHERE c.isDeleted = false ORDER BY c.title")
    List<Category> findAllActive();
    
    /**
     * Find all active categories with pagination
     */
    @Query("SELECT c FROM Category c WHERE c.isDeleted = false ORDER BY c.title")
    Page<Category> findAllActive(Pageable pageable);
    
    /**
     * Find categories with products count
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products p WHERE c.isDeleted = false AND (p IS NULL OR p.isDeleted = false)")
    List<Category> findAllWithActiveProducts();
    
    /**
     * Check if category title exists (excluding current category)
     */
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.title = :title AND c.isDeleted = false AND (:excludeId IS NULL OR c.id != :excludeId)")
    boolean existsByTitleAndNotDeleted(@Param("title") String title, @Param("excludeId") UUID excludeId);
    
    /**
     * Find category by ID and not deleted
     */
    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.isDeleted = false")
    Optional<Category> findByIdAndNotDeleted(@Param("id") UUID id);
    
    /**
     * Find categories by title containing and not deleted with pagination
     */
    @Query("SELECT c FROM Category c WHERE c.title ILIKE %:search% AND c.isDeleted = false ORDER BY c.title")
    Page<Category> findByTitleContainingIgnoreCaseAndNotDeleted(@Param("search") String search, Pageable pageable);
    
    /**
     * Find categories by status and not deleted with pagination
     */
    @Query("SELECT c FROM Category c WHERE c.status = :status AND c.isDeleted = false ORDER BY c.title")
    Page<Category> findByStatusAndNotDeleted(@Param("status") CategoryStatus status, Pageable pageable);
    
    /**
     * Find categories by title containing, status and not deleted with pagination
     */
    @Query("SELECT c FROM Category c WHERE c.title ILIKE %:search% AND c.status = :status AND c.isDeleted = false ORDER BY c.title")
    Page<Category> findByTitleContainingIgnoreCaseAndStatusAndNotDeleted(@Param("search") String search, @Param("status") CategoryStatus status, Pageable pageable);
}
