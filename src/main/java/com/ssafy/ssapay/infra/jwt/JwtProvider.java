package com.ssafy.ssapay.infra.jwt;

import com.ssafy.ssapay.domain.auth.dto.token.AccessToken;
import com.ssafy.ssapay.domain.auth.dto.token.RefreshToken;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.global.util.TimeUtil;
import com.ssafy.ssapay.infra.repository.TokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider implements InitializingBean {
    private static final String AUTHORITIES_KEY = "role";

    private final JwtProperties jwtProperties;
    private final JwtResolver jwtResolver;
    private final TokenRepository tokenRepository;

    private Key key;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    public LocalDateTime calAccessTokenExpirationTime(LocalDateTime now) {
        return now.plusSeconds(jwtProperties.getAccessTokenDuration().toSeconds());
    }

    public LocalDateTime calRefreshTokenExpirationTime(LocalDateTime now) {
        return now.plusSeconds(jwtProperties.getRefreshTokenDuration().toSeconds());
    }

    public String createToken(User user, LocalDateTime expirationTime) {
        String authorities = "USER";
        Date expiredDate = TimeUtil.convertLocalDateTimeToDate(expirationTime);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(expiredDate)
                .compact();
    }

    public int isValidToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return 1;
        } catch (ExpiredJwtException e) {
            return 0;
        } catch (IllegalArgumentException |
                 UnsupportedJwtException |
                 MalformedJwtException |
                 SignatureException e) {
            log.error(e.getMessage(), e);
        }
        return -1;
    }

    public boolean isLogoutAccessToken(String token) {
        String name = jwtResolver.getName(token);
        AccessToken accessToken = new AccessToken(name, token);

        return tokenRepository.exists(accessToken);
    }

    public boolean isLogoutRefreshToken(String token) {
        RefreshToken refreshToken = new RefreshToken(token);

        return tokenRepository.exists(refreshToken);
    }
}
