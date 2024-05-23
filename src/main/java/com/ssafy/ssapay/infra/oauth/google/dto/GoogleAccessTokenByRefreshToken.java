package com.ssafy.ssapay.infra.oauth.google.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record GoogleAccessTokenByRefreshToken(String tokenType,
                                              String accessToken,
                                              Integer expiresIn,
                                              String scope) {
}