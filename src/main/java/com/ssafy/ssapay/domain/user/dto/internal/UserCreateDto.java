package com.ssafy.ssapay.domain.user.dto.internal;

import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.domain.user.entity.UserSecret;

public record UserCreateDto(String username,
                            String encodedPassword,
                            String email,
                            String salt) {

    public User toUserEntity() {
        return User.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .authority("ROLE_USER")
                .build();
    }

    public UserSecret toUserSecretEntity() {
        return UserSecret.builder()
                .id(username)
                .salt(salt)
                .build();
    }
}
