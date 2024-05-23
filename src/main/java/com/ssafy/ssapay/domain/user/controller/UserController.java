package com.ssafy.ssapay.domain.user.controller;

import com.ssafy.ssapay.domain.user.dto.request.UserCreateRequest;
import com.ssafy.ssapay.domain.user.dto.response.UserResponse;
import com.ssafy.ssapay.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public void register(@RequestBody UserCreateRequest request) {
        userService.createUser(request);
    }

    @GetMapping("/profile")
    public UserResponse getUserProfile() {
        return userService.getUserProfile();
    }
}
