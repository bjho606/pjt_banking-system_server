package com.ssafy.ssapay.domain.token.dto;

import org.springframework.util.Assert;

public class RefreshToken extends Token {
    public static final String KEY_PREFIX = "refresh:";

    public RefreshToken(String key) {
        Assert.hasText(key, "key는 공백일 수 없습니다.");

        this.key = KEY_PREFIX + key;
    }
}
