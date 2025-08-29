package com.importer_ecommerce.importEcommerce.cart.repository;

import com.importer_ecommerce.importEcommerce.auth.entity.User;
import com.importer_ecommerce.importEcommerce.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    
    Optional<Cart> findByCustomer(User customer);
    
    Optional<Cart> findByCustomerId(UUID customerId);
    
    boolean existsByCustomerId(UUID customerId);
    
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items ci LEFT JOIN FETCH ci.productVariant pv LEFT JOIN FETCH pv.product p WHERE c.customer.id = :customerId")
    Optional<Cart> findByCustomerIdWithItems(@Param("customerId") UUID customerId);
    
    @Query("SELECT COUNT(ci) FROM Cart c JOIN c.items ci WHERE c.customer.id = :customerId")
    Long getItemCountByCustomerId(@Param("customerId") UUID customerId);
    
    void deleteByCustomerId(UUID customerId);
}
