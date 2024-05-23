package com.ssafy.ssapay.global.security;

import com.ssafy.ssapay.domain.auth.dto.internal.AuthenticatedToken;
import com.ssafy.ssapay.domain.auth.dto.internal.LoginUser;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.global.util.CookieUtil;
import com.ssafy.ssapay.infra.jwt.JwtProperties;
import com.ssafy.ssapay.infra.jwt.JwtProvider;
import com.ssafy.ssapay.infra.jwt.JwtResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final JwtResolver jwtResolver;
    private final JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveAccessToken(request);
        String refreshToken = resolveRefreshToken(request);
        String requestUri = request.getRequestURI();

        int result = verifyAccessToken(accessToken);
        if (result == 1) {
            handleSuccess(accessToken, refreshToken, requestUri);
        } else if (result == 0 && verifyRefreshToken(refreshToken)) {
            String newAccessToken = provideNewAccessToken(refreshToken);
            handleSuccess(newAccessToken, refreshToken, requestUri);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveAccessToken(HttpServletRequest request) {
        return CookieUtil.getCookieValue(request, jwtProperties.getAccessTokenCookieName())
                .orElse(null);
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        return CookieUtil.getCookieValue(request, jwtProperties.getRefreshTokenCookieName())
                .orElse(null);
    }

    private int verifyAccessToken(String accessToken) {
        if (!StringUtils.hasText(accessToken) || jwtProvider.isLogoutAccessToken(accessToken)) {
            return -1;
        }
        return jwtProvider.isValidToken(accessToken);
    }

    private void handleSuccess(String accessToken, String refreshToken, String requestUri) {
        User user = jwtResolver.getUser(accessToken);
        LoginUser loginUser = new LoginUser(user.getId(), user.getUsername());
        AuthenticatedToken authenticatedToken = new AuthenticatedToken(accessToken, refreshToken);
        UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken(loginUser,
                authenticatedToken, AuthorityUtils.createAuthorityList(user.getAuthority()));

        SecurityContextHolder.getContext().setAuthentication(loginToken);
        log.debug("{} stored in context: {}", user.getUsername(), requestUri);
    }

    private boolean verifyRefreshToken(String refreshToken) {
        return StringUtils.hasText(refreshToken) && !jwtProvider.isLogoutRefreshToken(refreshToken) && (
                jwtProvider.isValidToken(refreshToken) == 1);
    }

    private String provideNewAccessToken(String refreshToken) {
        User user = jwtResolver.getUser(refreshToken);
        LocalDateTime expiredTime = jwtProvider.calAccessTokenExpirationTime(LocalDateTime.now());
        return jwtProvider.createToken(user, expiredTime);
    }
}
