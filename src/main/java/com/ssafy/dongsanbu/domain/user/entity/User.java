package com.ssafy.dongsanbu.domain.user.entity;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User implements Serializable {
    private int id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String profileImage;
    private String authority;

    @Builder
    private User(int id,
                 String username,
                 String password,
                 String nickname,
                 String email,
                 String profileImage,
                 String authority) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.profileImage = profileImage;
        this.authority = authority;
    }
}