package com.cartigo.user.mapper;

import com.cartigo.user.dto.UserResponse;
import com.cartigo.user.entity.User;

public class UserMapper {

    private UserMapper() {}

    public static UserResponse toResponse(User user) {
        UserResponse r = new UserResponse();
        r.setId(user.getId());
        r.setFirstName(user.getFirstName());
        r.setLastName(user.getLastName());
        r.setEmail(user.getEmail());
        r.setRole(user.getRole());
        r.setIsActive(user.getIsActive());
        r.setCreatedAt(user.getCreatedAt());
        return r;
    }
}
