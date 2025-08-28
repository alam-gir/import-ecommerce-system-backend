package com.importer_ecommerce.importEcommerce.product.repository;

import com.importer_ecommerce.importEcommerce.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    
    Optional<Category> findByTitle(String title);
    
    boolean existsByTitle(String title);
    
    boolean existsByTitleAndIdNot(String title, UUID id);
    
    @Query("SELECT c FROM Category c WHERE c.title LIKE %:searchTerm% OR c.description LIKE %:searchTerm%")
    List<Category> findByTitleOrDescriptionContaining(@Param("searchTerm") String searchTerm);
    
    List<Category> findByTitleContainingIgnoreCase(String title);
    
    // Find root categories (no parent)
    List<Category> findByParentIsNull();
    
    // Find children of a specific category
    List<Category> findByParentId(UUID parentId);
    
    // Check if category has children
    boolean existsByParentId(UUID parentId);
}
