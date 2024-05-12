package com.ssafy.dongsanbu.global.filter;

import com.ssafy.dongsanbu.domain.user.entity.User;
import com.ssafy.dongsanbu.global.util.CookieUtil;
import com.ssafy.dongsanbu.infra.jwt.JwtProperties;
import com.ssafy.dongsanbu.infra.jwt.JwtProvider;
import com.ssafy.dongsanbu.infra.jwt.JwtResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final JwtResolver jwtResolver;
    private final JwtProperties jwtProperties;

    private static void handleUnsuccess(String requestUri, HttpServletResponse response) {
        log.debug("No valid token: {}", requestUri);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (isLoginOrSignupRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = resolveAccessToken(request);
        String refreshToken = resolveRefreshToken(request);
        String requestUri = request.getRequestURI();

        log.debug(requestUri);
        log.debug(accessToken);
        log.debug(refreshToken);
        if (verify(accessToken)) {
            handleSuccess(accessToken, requestUri);
        } else if (verify(refreshToken)) {
            createNewAccessTokenAndHandleSuccess(response, refreshToken);
        } else {
            handleUnsuccess(requestUri, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isLoginOrSignupRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.equals("/api/v1/auth/login");
    }

    private String resolveAccessToken(HttpServletRequest request) {
        return CookieUtil.getCookieValue(request, jwtProperties.getAccessTokenCookieName())
                .orElse(null);
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        return CookieUtil.getCookieValue(request, jwtProperties.getRefreshTokenCookieName())
                .orElse(null);
    }

    private boolean verify(String token) {
        return StringUtils.hasText(token)
                && jwtProvider.isValidToken(token);
    }

    private void handleSuccess(String token, String requestUri) {
        User user = jwtResolver.getUser(token);
        log.debug("{} stored in context: {}", user.getUsername(), requestUri);
    }

    private void createNewAccessTokenAndHandleSuccess(
            HttpServletResponse response, String refreshToken) {
        User user = jwtResolver.getUser(refreshToken);
        LocalDateTime expiredTime = jwtProvider.calAccessTokenExpirationTime(LocalDateTime.now());
        String accessToken = jwtProvider.createToken(user, expiredTime);

        CookieUtil.addCookie(response, jwtProperties.getAccessTokenCookieName(), accessToken,
                jwtProperties.getAccessTokenDuration().getSeconds());
    }
}
