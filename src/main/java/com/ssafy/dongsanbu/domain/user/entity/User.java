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
    private int point;
    private String authority;
    private int version;

    @Builder
    private User(int id,
                 String username,
                 String password,
                 String nickname,
                 String email,
                 String profileImage,
                 int point,
                 String authority,
                 int version) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.profileImage = profileImage;
        this.point = point;
        this.authority = authority;
        this.version = version;
    }

    public void addPoint(int pointAmount) {
        if(pointAmount < 0) {
            throw new IllegalArgumentException("Point amount must be positive");
        }
        this.point += pointAmount;
    }

    public void usePoint(int pointAmount) {
        if(pointAmount <= 0) {
            throw new IllegalArgumentException("Point amount must be positive");
        }
        if(this.point < pointAmount) {
            throw new IllegalArgumentException("Not enough point");
        }
        this.point -= pointAmount;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", point=" + point +
                ", authority='" + authority + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}