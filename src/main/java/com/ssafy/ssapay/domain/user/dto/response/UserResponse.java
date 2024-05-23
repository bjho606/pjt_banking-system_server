package com.ssafy.ssapay.domain.user.dto.response;

import com.ssafy.ssapay.domain.user.entity.User;

public record UserResponse(String username,
                           String email) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getUsername(),
                user.getEmail());
    }
}
