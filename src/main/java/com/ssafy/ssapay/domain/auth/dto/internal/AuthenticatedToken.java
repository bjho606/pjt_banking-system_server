package com.ssafy.ssapay.domain.auth.dto.internal;

public record AuthenticatedToken(String accessToken,
                                 String refreshToken) {
}
