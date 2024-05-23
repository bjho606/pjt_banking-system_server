package com.ssafy.ssapay.infra.oauth.google.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ssafy.ssapay.domain.oauth.OauthServerType;
import com.ssafy.ssapay.domain.user.entity.OauthId;
import com.ssafy.ssapay.domain.user.entity.User;

@JsonNaming(SnakeCaseStrategy.class)
public record GoogleMemberResponse(String sub,
                                   String name,
                                   String givenName,
                                   String familyName,
                                   String picture,
                                   String email,
                                   Boolean emailVerified,
                                   String locale) {

    public User toEntity(String refreshToken) {
        return User.builder()
                .oauthId(new OauthId(sub, OauthServerType.GOOGLE))
                .nickname(name)
                .email(email)
                .authority("ROLE_USER")
                .refreshToken(refreshToken)
                .build();
    }
}