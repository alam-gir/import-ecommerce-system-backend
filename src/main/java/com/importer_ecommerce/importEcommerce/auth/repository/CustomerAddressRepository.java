package com.importer_ecommerce.importEcommerce.auth.repository;

import com.importer_ecommerce.importEcommerce.auth.entity.CustomerAddress;
import com.importer_ecommerce.importEcommerce.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {
    
    List<CustomerAddress> findByUser(User user);
    
    List<CustomerAddress> findByUserAndAddressType(User user, CustomerAddress.AddressType addressType);
    
    Optional<CustomerAddress> findByUserAndIsDefaultTrue(User user);
    
    Optional<CustomerAddress> findByUserAndAddressTypeAndIsDefaultTrue(User user, CustomerAddress.AddressType addressType);
}

