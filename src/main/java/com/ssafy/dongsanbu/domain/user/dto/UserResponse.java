package com.ssafy.dongsanbu.domain.user.dto;

import com.ssafy.dongsanbu.domain.user.entity.User;

public record UserResponse(String nickname,
                           String email,
                           String profileImage) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getNickname(),
                user.getEmail(),
                user.getProfileImage());
    }
}
