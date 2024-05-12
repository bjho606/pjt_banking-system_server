package com.ssafy.dongsanbu.infra.jwt;

import com.ssafy.dongsanbu.domain.token.dto.AccessToken;
import com.ssafy.dongsanbu.domain.token.mapper.LogoutTokenMapper;
import com.ssafy.dongsanbu.domain.user.entity.User;
import com.ssafy.dongsanbu.global.util.TimeUtil;
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

    private final LogoutTokenMapper logoutTokenMapper;
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
                .setSubject(user.getUsername())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(expiredDate)
                .compact();
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException |
                 IllegalArgumentException |
                 UnsupportedJwtException |
                 MalformedJwtException |
                 SignatureException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public boolean isLogoutAccessToken(String token) {
        String name = jwtResolver.getName(token);
        AccessToken accessToken = new AccessToken(name, token);

        return logoutTokenMapper.exists(accessToken);
    }
}
