package com.ssafy.ssapay.domain.auth.service;

import com.ssafy.ssapay.domain.auth.dto.internal.AuthenticatedToken;
import com.ssafy.ssapay.domain.auth.dto.internal.LoginUser;
import com.ssafy.ssapay.domain.auth.dto.request.LoginRequest;
import com.ssafy.ssapay.domain.auth.dto.response.LoginResponse;
import com.ssafy.ssapay.domain.auth.implementation.TokenManager;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.domain.user.implementation.UserReader;
import com.ssafy.ssapay.global.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserReader userReader;
    private final TokenManager tokenManager;

    public LoginResponse login(LoginRequest request) {
        User user = userReader.getUserByUsernameAndPassword(request);

        return provideToken(user);
    }

    public LoginResponse login(User user) {
        return provideToken(user);
    }

    private LoginResponse provideToken(User user) {
        String accessToken = tokenManager.createAccessToken(user);
        String refreshToken = tokenManager.createRefreshToken(user);
        return new LoginResponse(refreshToken, accessToken);
    }

    @Transactional
    public void logout() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) AuthenticationUtil.getAuthentication();
        AuthenticatedToken credentials = (AuthenticatedToken) authentication.getCredentials();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        tokenManager.logoutAccessToken(credentials.accessToken());
        tokenManager.logoutRefreshToken(loginUser.username());
    }
}
