package com.ssafy.ssapay.domain.auth.implementation;

import com.ssafy.ssapay.domain.auth.dto.token.AccessToken;
import com.ssafy.ssapay.domain.auth.dto.token.RefreshToken;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.infra.jwt.JwtProvider;
import com.ssafy.ssapay.infra.jwt.JwtResolver;
import com.ssafy.ssapay.infra.repository.TokenRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
@Transactional(readOnly = true)
public class TokenManager {
    private final JwtResolver jwtResolver;
    private final JwtProvider jwtProvider;
    private final TokenRepository tokenRepository;

    public String createAccessToken(User user) {
        LocalDateTime expiredTime = jwtProvider.calAccessTokenExpirationTime(LocalDateTime.now());
        return jwtProvider.createToken(user, expiredTime);
    }

    public String createRefreshToken(User user) {
        LocalDateTime expiredTime = jwtProvider.calRefreshTokenExpirationTime(LocalDateTime.now());
        return jwtProvider.createToken(user, expiredTime);
    }

    @Transactional
    public void logoutAccessToken(String accessToken) {
        String username = jwtResolver.getName(accessToken);
        long expiration = jwtResolver.getExpirationTime(accessToken);
        AccessToken token = new AccessToken(username, accessToken);
        tokenRepository.save(token, expiration);
    }

    @Transactional
    public void logoutRefreshToken(String username) {
        tokenRepository.delete(new RefreshToken(username));
    }
}
