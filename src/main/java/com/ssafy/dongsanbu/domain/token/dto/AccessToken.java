package com.ssafy.dongsanbu.domain.token.dto;

import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class AccessToken extends Token {
    public static final String KEY_PREFIX = "access:";

    public AccessToken(String key, String value) {
        Assert.hasText(key, "key는 공백일 수 없습니다.");

        this.key = KEY_PREFIX + key;
        this.value = value;
    }

    public AccessToken(String key) {
        Assert.hasText(key, "key는 공백일 수 없습니다.");

        this.key = KEY_PREFIX + key;
    }
}
