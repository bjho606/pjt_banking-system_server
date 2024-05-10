package com.ssafy.dongsanbu.domain.user.dto;


import com.ssafy.dongsanbu.domain.user.entity.User;

public record UserCreateRequest(String username,
                                String password,
                                String nickname,
                                String email) {

    public User toUserEntity(String password) {
        return User.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .email(email)
                .authority("ROLE_USER")
                .build();
    }
}
