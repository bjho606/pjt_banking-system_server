//package com.ssafy.ssapay.domain.auth.service;
//
//import com.ssafy.ssapay.domain.auth.dto.LoginDto;
//import com.ssafy.ssapay.domain.auth.dto.LoginRequest;
//import com.ssafy.ssapay.domain.auth.dto.LoginResponse;
//import com.ssafy.ssapay.domain.user.entity.UserSecret;
//import com.ssafy.ssapay.domain.user.entity.User;
//import com.ssafy.ssapay.global.error.type.BadRequestException;
//import com.ssafy.ssapay.global.util.MyCrypt;
//import com.ssafy.ssapay.infra.jwt.JwtProvider;
//import com.ssafy.ssapay.infra.jwt.JwtResolver;
//import java.time.LocalDateTime;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//    private final JwtResolver jwtResolver;
//    private final JwtProvider jwtProvider;
//
//    public LoginResponse login(LoginRequest request) {
//        User user = findUserByUsernameAndPassword(request);
//
//        String accessToken = createAccessToken(user);
//        String refreshToken = createRefreshToken(user);
//        return new LoginResponse(refreshToken, accessToken);
//    }
//
//    private User findUserByUsernameAndPassword(LoginRequest request) {
//        UserSecret userSecret = userMapper.findIngredientById(request.username());
//        String encodedPassword = MyCrypt.byteArrayToHex(
//                MyCrypt.getSHA256(request.password(), userSecret.getSalt()));
//
//        LoginDto loginDto = new LoginDto(request.username(), encodedPassword);
//        User user = authMapper.findByUsernameAndPassword(loginDto);
//        if (user == null) {
//            throw new BadRequestException("아이디 또는 비밀번호가 일치하지 않습니다.");
//        }
//
//        return user;
//    }
//
//    private String createAccessToken(User user) {
//        LocalDateTime expiredTime = jwtProvider.calAccessTokenExpirationTime(LocalDateTime.now());
//        return jwtProvider.createToken(user, expiredTime);
//    }
//
//    private String createRefreshToken(User user) {
//        LocalDateTime expiredTime = jwtProvider.calRefreshTokenExpirationTime(LocalDateTime.now());
//        return jwtProvider.createToken(user, expiredTime);
//    }
//
//    @Transactional
//    public void logout(String accessToken) {
//        String memberName = jwtResolver.getName(accessToken);
//
//        logoutAccessToken(memberName, accessToken);
//        logoutRefreshToken(memberName);
//    }
//
//    private void logoutAccessToken(String memberName, String accessToken) {
//        long expiration = jwtResolver.getExpirationTime(accessToken);
//        logoutTokenMapper.save(new AccessToken(memberName, accessToken), expiration);
//    }
//
//    private void logoutRefreshToken(String memberName) {
//        logoutTokenMapper.delete(new RefreshToken(memberName));
//    }
//}
