package com.ssafy.ssapay.domain.auth.dto;

public record LoginResponse(String refreshToken,
                            String accessToken) {
}
