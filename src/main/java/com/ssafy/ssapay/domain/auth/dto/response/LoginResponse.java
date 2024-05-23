package com.ssafy.ssapay.domain.auth.dto.response;

public record LoginResponse(String refreshToken,
                            String accessToken) {
}
