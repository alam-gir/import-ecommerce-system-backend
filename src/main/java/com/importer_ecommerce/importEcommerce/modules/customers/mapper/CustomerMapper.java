package com.importer_ecommerce.importEcommerce.modules.customers.mapper;

import com.importer_ecommerce.importEcommerce.modules.customers.dto.response.CustomerResponse;
import com.importer_ecommerce.importEcommerce.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return new CustomerResponse(
                user.getId(),
                user.getPhone(),
                user.getName(),
                user.getEmail(),
                user.getProfileImage(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
