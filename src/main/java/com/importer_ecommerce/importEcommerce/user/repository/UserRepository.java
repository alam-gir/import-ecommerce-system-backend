package com.importer_ecommerce.importEcommerce.user.repository;

import com.importer_ecommerce.importEcommerce.user.entity.User;
import com.importer_ecommerce.importEcommerce.user.entity.UserRole;
import com.importer_ecommerce.importEcommerce.user.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by phone
     */
    Optional<User> findByPhone(String phone);
    
    /**
     * Find user by email or phone
     */
    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.phone = :identifier")
    Optional<User> findByEmailOrPhone(@Param("identifier") String identifier);
    
    /**
     * Find admin users by role
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isDeleted = false")
    java.util.List<User> findByRole(@Param("role") UserRole role);
    
    /**
     * Find active users by role
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.status = :status AND u.isDeleted = false")
    java.util.List<User> findByRoleAndStatus(@Param("role") UserRole role, @Param("status") UserStatus status);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if phone exists
     */
    boolean existsByPhone(String phone);
    
    /**
     * Check if email exists excluding specific user
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.id != :excludeId")
    boolean existsByEmailExcludingId(@Param("email") String email, @Param("excludeId") UUID excludeId);
    
    /**
     * Check if phone exists excluding specific user
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.phone = :phone AND u.id != :excludeId")
    boolean existsByPhoneExcludingId(@Param("phone") String phone, @Param("excludeId") UUID excludeId);
}
