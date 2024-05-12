package com.ssafy.dongsanbu.infra.jwt;

import java.time.Duration;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties("jwt")
@Getter
public class JwtProperties {
    private final String accessTokenCookieName;
    private final String refreshTokenCookieName;
    private final String secret;
    private final Duration accessTokenDuration;
    private final Duration refreshTokenDuration;

    @ConstructorBinding
    public JwtProperties(String accessTokenCookieName, String refreshTokenCookieName,
                         String secret, Duration accessTokenDuration, Duration refreshTokenDuration) {
        this.accessTokenCookieName = accessTokenCookieName;
        this.refreshTokenCookieName = refreshTokenCookieName;
        this.secret = secret;
        this.accessTokenDuration = accessTokenDuration;
        this.refreshTokenDuration = refreshTokenDuration;
    }
}