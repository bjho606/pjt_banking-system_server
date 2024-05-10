package com.ssafy.dongsanbu.domain.auth.controller;

import com.ssafy.dongsanbu.domain.auth.dto.AuthRequest;
import com.ssafy.dongsanbu.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final AuthService authService;

    @PostMapping("/login")
    public void login(HttpServletRequest request,
                      @RequestBody AuthRequest requestBody) {
        authService.login(request, requestBody);
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request) {
        authService.logout(request);
    }
}
