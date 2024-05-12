package com.ssafy.dongsanbu.domain.auth.controller;

import com.ssafy.dongsanbu.domain.auth.dto.LoginRequest;
import com.ssafy.dongsanbu.domain.auth.dto.LoginResponse;
import com.ssafy.dongsanbu.domain.auth.service.AuthService;
import com.ssafy.dongsanbu.global.util.CookieUtil;
import com.ssafy.dongsanbu.infra.jwt.JwtProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtProperties jwtProperties;
    private final AuthService authService;

    @PostMapping("/login")
    public void login(@RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse token = authService.login(request);
        CookieUtil.addCookie(response,
                jwtProperties.getAccessTokenCookieName(),
                token.accessToken(),
                jwtProperties.getAccessTokenDuration().getSeconds());
        CookieUtil.addCookie(response,
                jwtProperties.getRefreshTokenCookieName(),
                token.refreshToken(),
                jwtProperties.getRefreshTokenDuration().getSeconds());
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = CookieUtil.getCookieValue(request, jwtProperties.getAccessTokenCookieName())
                .orElseThrow(() -> new IllegalStateException("쿠키에서 access token을 가져올 수 없습니다."));
        authService.logout(accessToken);

        CookieUtil.deleteCookie(request, response, jwtProperties.getAccessTokenCookieName());
        CookieUtil.deleteCookie(request, response, jwtProperties.getRefreshTokenCookieName());
    }
}
