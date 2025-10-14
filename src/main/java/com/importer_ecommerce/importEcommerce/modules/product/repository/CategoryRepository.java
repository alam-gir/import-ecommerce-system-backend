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
     * Find all categories with pagination and filtering
     */
    Page<Category> findByTitleContainingIgnoreCaseAndStatus(String title, CategoryStatus status, Pageable pageable);
    
    /**
     * Find all categories with pagination and filtering by title only
     */
    Page<Category> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    /**
     * Find all categories with pagination and filtering by status only
     */
    Page<Category> findByStatus(CategoryStatus status, Pageable pageable);
    
    /**
     * Check if category title exists (excluding current category)
     */
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.title = :title AND (:excludeId IS NULL OR c.id != :excludeId)")
    boolean existsByTitleAndNotDeleted(@Param("title") String title, @Param("excludeId") UUID excludeId);
    
    /**
     * Find root categories (categories with no parent)
     */
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL ORDER BY c.title")
    List<Category> findRootCategories();
    
    /**
     * Find child categories by parent ID
     */
    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId ORDER BY c.title")
    List<Category> findByParentId(@Param("parentId") UUID parentId);
    
    /**
     * Find all categories in hierarchical order (root first, then children)
     */
    @Query("SELECT c FROM Category c ORDER BY c.parent.id NULLS FIRST, c.title")
    List<Category> findAllHierarchical();
    
    /**
     * Find category with its parent and children
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.parent LEFT JOIN FETCH c.children WHERE c.id = :id")
    Optional<Category> findByIdWithParentAndChildren(@Param("id") UUID id);
    
    /**
     * Check if category has children
     */
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.parent.id = :parentId")
    boolean hasChildren(@Param("parentId") UUID parentId);
    
    /**
     * Find root categories (level 0)
     */
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL ORDER BY c.title")
    Page<Category> findRootCategoriesPage(Pageable pageable);
    
    /**
     * Find categories by level using recursive approach
     */
    @Query("SELECT c FROM Category c WHERE c.parent IS NOT NULL ORDER BY c.title")
    List<Category> findAllWithParent();
    
    /**
     * Get maximum level of categories in the database using a simpler approach
     */
    @Query("SELECT MAX(" +
           "CASE " +
           "WHEN c.parent IS NULL THEN 0 " +
           "ELSE 1 " +
           "END) FROM Category c")
    Integer findMaxLevelSimple();
}
