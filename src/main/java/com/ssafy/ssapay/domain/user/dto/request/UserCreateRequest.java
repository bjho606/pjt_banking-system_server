package com.ssafy.ssapay.domain.user.dto.request;


public record UserCreateRequest(String username,
                                String password,
                                String email) {
}
