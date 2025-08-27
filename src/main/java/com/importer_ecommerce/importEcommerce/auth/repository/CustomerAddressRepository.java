package com.importer_ecommerce.importEcommerce.auth.repository;

import com.importer_ecommerce.importEcommerce.auth.entity.CustomerAddress;
import com.importer_ecommerce.importEcommerce.auth.entity.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, UUID> {
    
    List<CustomerAddress> findByCustomer(CustomerProfile customer);
    
    List<CustomerAddress> findByCustomerAndAddressType(CustomerProfile customer, CustomerAddress.AddressType addressType);
}

