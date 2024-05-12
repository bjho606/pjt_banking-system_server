package com.ssafy.dongsanbu.domain.auth.dto;

public record LoginResponse(String refreshToken,
                            String accessToken) {
}
