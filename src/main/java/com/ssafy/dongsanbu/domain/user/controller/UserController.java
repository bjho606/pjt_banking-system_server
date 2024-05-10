package com.ssafy.dongsanbu.domain.user.controller;

import com.ssafy.dongsanbu.domain.user.dto.UserResponse;
import com.ssafy.dongsanbu.domain.user.dto.UserUpdateRequest;
import com.ssafy.dongsanbu.domain.user.dto.UserCreateRequest;
import com.ssafy.dongsanbu.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserResponse findUser(@PathVariable int id) {
        return userService.getUser(id);
    }

    @PostMapping
    public void register(@RequestBody UserCreateRequest request) {
        userService.createUser(request);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable int id,
                           @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
    }

    @PostMapping("/{id}/profile")
    public void updateProfileImage(@PathVariable int id,
                                   @RequestPart("file") MultipartFile file) {
        userService.updateProfileImage(id, file);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }
}
