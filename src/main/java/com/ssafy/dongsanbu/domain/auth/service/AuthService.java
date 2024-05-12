package com.ssafy.dongsanbu.domain.auth.service;

import com.ssafy.dongsanbu.domain.auth.dto.LoginDto;
import com.ssafy.dongsanbu.domain.auth.dto.LoginRequest;
import com.ssafy.dongsanbu.domain.auth.dto.LoginResponse;
import com.ssafy.dongsanbu.domain.auth.mapper.AuthMapper;
import com.ssafy.dongsanbu.domain.token.dto.AccessToken;
import com.ssafy.dongsanbu.domain.token.dto.RefreshToken;
import com.ssafy.dongsanbu.domain.token.mapper.LogoutTokenMapper;
import com.ssafy.dongsanbu.domain.user.entity.Ingredient;
import com.ssafy.dongsanbu.domain.user.entity.User;
import com.ssafy.dongsanbu.domain.user.mapper.UserMapper;
import com.ssafy.dongsanbu.global.error.type.BadRequestException;
import com.ssafy.dongsanbu.global.util.MyCrypt;
import com.ssafy.dongsanbu.infra.jwt.JwtProvider;
import com.ssafy.dongsanbu.infra.jwt.JwtResolver;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthMapper authMapper;
    private final UserMapper userMapper;
    private final LogoutTokenMapper logoutTokenMapper;
    private final JwtResolver jwtResolver;
    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginRequest request) {
        User user = findUserByUsernameAndPassword(request);

        String accessToken = createAccessToken(user);
        String refreshToken = createRefreshToken(user);
        return new LoginResponse(refreshToken, accessToken);
    }

    private User findUserByUsernameAndPassword(LoginRequest request) {
        Ingredient ingredient = userMapper.findIngredientById(request.username());
        String encodedPassword = MyCrypt.byteArrayToHex(
                MyCrypt.getSHA256(request.password(), ingredient.getSalt()));

        LoginDto loginDto = new LoginDto(request.username(), encodedPassword);
        User user = authMapper.findByUsernameAndPassword(loginDto);
        if (user == null) {
            throw new BadRequestException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    private String createAccessToken(User user) {
        LocalDateTime expiredTime = jwtProvider.calAccessTokenExpirationTime(LocalDateTime.now());
        return jwtProvider.createToken(user, expiredTime);
    }

    private String createRefreshToken(User user) {
        LocalDateTime expiredTime = jwtProvider.calRefreshTokenExpirationTime(LocalDateTime.now());
        return jwtProvider.createToken(user, expiredTime);
    }

    @Transactional
    public void logout(String accessToken) {
        String memberName = jwtResolver.getName(accessToken);

        logoutAccessToken(memberName, accessToken);
        logoutRefreshToken(memberName);
    }

    private void logoutAccessToken(String memberName, String accessToken) {
        long expiration = jwtResolver.getExpirationTime(accessToken);
        logoutTokenMapper.save(new AccessToken(memberName, accessToken), expiration);
    }

    private void logoutRefreshToken(String memberName) {
        logoutTokenMapper.delete(new RefreshToken(memberName));
    }
}
