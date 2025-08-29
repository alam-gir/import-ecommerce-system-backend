package com.importer_ecommerce.importEcommerce.cart.repository;

import com.importer_ecommerce.importEcommerce.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    
    List<CartItem> findByCartId(UUID cartId);
    
    Optional<CartItem> findByCartIdAndProductVariantId(UUID cartId, UUID productVariantId);
    
    boolean existsByCartIdAndProductVariantId(UUID cartId, UUID productVariantId);
    
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.customer.id = :customerId AND ci.productVariant.id = :variantId")
    Optional<CartItem> findByCustomerIdAndVariantId(@Param("customerId") UUID customerId, @Param("variantId") UUID variantId);
    
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.customer.id = :customerId AND ci.productVariant.id = :variantId")
    Long countByCustomerIdAndVariantId(@Param("customerId") UUID customerId, @Param("variantId") UUID variantId);
    
    void deleteByCartId(UUID cartId);
    
    void deleteByCartIdAndProductVariantId(UUID cartId, UUID productVariantId);
}
