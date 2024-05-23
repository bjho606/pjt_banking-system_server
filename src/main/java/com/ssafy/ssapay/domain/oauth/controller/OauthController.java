package com.ssafy.ssapay.domain.oauth.controller;


import com.ssafy.ssapay.domain.auth.dto.response.LoginResponse;
import com.ssafy.ssapay.domain.auth.service.AuthService;
import com.ssafy.ssapay.domain.oauth.dto.AuthCode;
import com.ssafy.ssapay.domain.oauth.service.OAuthService;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.global.util.CookieUtil;
import com.ssafy.ssapay.infra.jwt.JwtProperties;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/oauth")
@RequiredArgsConstructor
@Slf4j
public class OauthController {
    private final JwtProperties jwtProperties;
    private final OAuthService oauthService;
    private final AuthService authService;

    @GetMapping("/{oauthServerType}")
    public void redirectAuthCodeRequestUrl(@PathVariable String oauthServerType,
                                           HttpServletResponse response) throws IOException {
        String redirectUrl = oauthService.getAuthCodeRequestUrl(oauthServerType);

        response.sendRedirect(redirectUrl);
    }

    @PostMapping("/login/{oauthServerType}")
    public void login(@PathVariable String oauthServerType,
                      @RequestBody AuthCode authCode,
                      HttpServletResponse response) {
        User user = oauthService.loginOrSignup(oauthServerType, authCode.code());

        LoginResponse token = authService.login(user);
        CookieUtil.addCookie(response,
                jwtProperties.getAccessTokenCookieName(),
                token.accessToken(),
                jwtProperties.getAccessTokenDuration().getSeconds());
        CookieUtil.addCookie(response,
                jwtProperties.getRefreshTokenCookieName(),
                token.refreshToken(),
                jwtProperties.getRefreshTokenDuration().getSeconds());
    }
}