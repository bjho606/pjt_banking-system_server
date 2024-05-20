package com.ssafy.ssapay.domain.user.controller;

import com.ssafy.ssapay.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

}
