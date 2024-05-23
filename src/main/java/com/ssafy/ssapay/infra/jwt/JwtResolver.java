package com.ssafy.ssapay.infra.jwt;

import com.ssafy.ssapay.domain.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtResolver implements InitializingBean {

    private static final String AUTHORITIES_KEY = "role";

    private final JwtProperties jwtProperties;
    private Key key;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims getClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getName(String token) {
        return getClaims(token).getSubject();
    }

    public String getAuthorities(String token) {
        return getClaims(token).get(AUTHORITIES_KEY).toString();
    }

    public long getExpirationTime(String token) {
        return getClaims(token).getExpiration().getTime();
    }

    public User getUser(String token) {
        Claims claims = getClaims(token);
        String role = claims.get(AUTHORITIES_KEY).toString();

        return User.builder()
                .username(claims.getSubject())
                .authority(role)
                .build();
    }
}
