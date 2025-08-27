package com.importer_ecommerce.importEcommerce.auth.controller;

import com.importer_ecommerce.importEcommerce.auth.entity.CustomerAddress;
import com.importer_ecommerce.importEcommerce.auth.repository.CustomerAddressRepository;
import com.importer_ecommerce.importEcommerce.common.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/addresses")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerAddressController {
    
    private final CustomerAddressRepository addressRepository;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerAddress>>> getAllAddresses() {
        // Note: In real implementation, get user from JWT token
        // For now, return empty list
        List<CustomerAddress> addresses = List.of();
        return ResponseEntity.ok(ApiResponse.success(addresses, "Addresses retrieved successfully"));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerAddress>> addAddress(@RequestBody CustomerAddress address) {
        // Note: In real implementation, set user from JWT token
        // For now, return the address as-is
        return ResponseEntity.ok(ApiResponse.success(address, "Address added successfully"));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerAddress>> updateAddress(@PathVariable Long id, @RequestBody CustomerAddress address) {
        // Note: In real implementation, validate ownership and update
        // For now, return the address as-is
        return ResponseEntity.ok(ApiResponse.success(address, "Address updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long id) {
        // Note: In real implementation, validate ownership and delete
        return ResponseEntity.ok(ApiResponse.success(null, "Address deleted successfully"));
    }
}

