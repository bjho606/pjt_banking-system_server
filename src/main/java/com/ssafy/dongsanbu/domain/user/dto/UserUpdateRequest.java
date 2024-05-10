package com.ssafy.dongsanbu.domain.user.dto;

import com.ssafy.dongsanbu.domain.user.entity.User;

public record UserUpdateRequest(String nickname,
                                String email) {

    public User toUserEntity(int id) {
        return User.builder()
                .id(id)
                .nickname(nickname)
                .email(email)
                .build();
    }
}

