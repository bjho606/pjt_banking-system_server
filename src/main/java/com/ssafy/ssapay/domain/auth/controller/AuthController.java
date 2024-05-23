package com.ssafy.ssapay.domain.auth.controller;


import com.ssafy.ssapay.domain.auth.dto.request.LoginRequest;
import com.ssafy.ssapay.domain.auth.dto.response.LoginResponse;
import com.ssafy.ssapay.domain.auth.service.AuthService;
import com.ssafy.ssapay.global.util.CookieUtil;
import com.ssafy.ssapay.infra.jwt.JwtProperties;
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
    public void login(@RequestBody LoginRequest request,
                      HttpServletResponse response) {
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
    public void logout(HttpServletRequest request,
                       HttpServletResponse response) {
        authService.logout();

        CookieUtil.deleteCookie(request, response, jwtProperties.getAccessTokenCookieName());
        CookieUtil.deleteCookie(request, response, jwtProperties.getRefreshTokenCookieName());
    }
}
