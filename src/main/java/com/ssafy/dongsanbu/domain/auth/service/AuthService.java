package com.ssafy.dongsanbu.domain.auth.service;

import com.ssafy.dongsanbu.domain.auth.dto.AuthRequest;
import com.ssafy.dongsanbu.domain.auth.dto.LoginDto;
import com.ssafy.dongsanbu.domain.auth.mapper.AuthMapper;
import com.ssafy.dongsanbu.domain.user.entity.Ingredient;
import com.ssafy.dongsanbu.domain.user.entity.User;
import com.ssafy.dongsanbu.domain.user.mapper.UserMapper;
import com.ssafy.dongsanbu.global.error.type.BadRequestException;
import com.ssafy.dongsanbu.global.util.MyCrypt;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthMapper authMapper;
    private final UserMapper userMapper;

    public void login(HttpServletRequest request,
                      AuthRequest requestBody) {
        Ingredient ingredient = userMapper.findIngredientById(requestBody.username());
        String encodedPassword = MyCrypt.byteArrayToHex(
                MyCrypt.getSHA256(requestBody.password(), ingredient.getSalt()));

        User user = authMapper.findByUsernameAndPassword(new LoginDto(requestBody.username(),
                encodedPassword));

        if (user == null) {
            throw new BadRequestException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        HttpSession session = request.getSession();
        session.setAttribute("user", user);
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            throw new BadRequestException("로그인 상태가 아닙니다.");
        }

        session.invalidate();
    }
}
