package com.importer_ecommerce.importEcommerce.user.mapper;

import com.importer_ecommerce.importEcommerce.user.dto.request.UserCreateRequest;
import com.importer_ecommerce.importEcommerce.user.dto.response.UserResponse;
import com.importer_ecommerce.importEcommerce.user.entity.User;
import com.importer_ecommerce.importEcommerce.user.entity.UserStatus;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for User related DTOs
 */
@Component
public class UserMapper {
    
    /**
     * Convert User entity to UserResponse
     */
    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }
        
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhone(),
            user.getProfileImage(),
            user.getRole(),
            user.getStatus(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
    
    /**
     * Convert UserCreateRequest to User entity
     */
    public User toUserEntity(UserCreateRequest request) {
        if (request == null) {
            return null;
        }
        
        return User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .profileImage(request.getProfileImage())
            .role(request.getRole())
            .status(UserStatus.ACTIVE)
            .build();
    }
    
    /**
     * Convert list of User entities to list of UserResponse
     */
    public List<UserResponse> toUserResponseList(List<User> users) {
        if (users == null) {
            return Collections.emptyList();
        }
        
        return users.stream()
            .map(this::toUserResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Update User entity with request data
     */
    public void updateUserFromRequest(User user, UserCreateRequest request) {
        if (user == null || request == null) {
            return;
        }
        
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setProfileImage(request.getProfileImage());
        user.setRole(request.getRole());
    }
}
