package com.ssafy.ssapay.infra.oauth.google.client;

import com.ssafy.ssapay.domain.oauth.OauthServerType;
import com.ssafy.ssapay.domain.oauth.client.OauthMemberClient;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.infra.oauth.google.config.GoogleOauthConfig;
import com.ssafy.ssapay.infra.oauth.google.dto.GoogleAccessTokenByCode;
import com.ssafy.ssapay.infra.oauth.google.dto.GoogleAccessTokenByRefreshToken;
import com.ssafy.ssapay.infra.oauth.google.dto.GoogleMemberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleMemberClient implements OauthMemberClient {

    private final GoogleApiClient googleApiClient;
    private final GoogleOauthConfig googleOauthConfig;

    @Override
    public OauthServerType supportServer() {
        return OauthServerType.GOOGLE;
    }

    @Override
    public User fetch(String code) {
        String redirectUri = googleOauthConfig.redirectUri();

        GoogleAccessTokenByCode googleAccessTokenByCode = googleApiClient.fetchToken("authorization_code",
                googleOauthConfig.clientId(), googleOauthConfig.clientSecret(), code, redirectUri);
        GoogleMemberResponse googleMemberResponse = googleApiClient.fetchProfile(googleAccessTokenByCode.accessToken());

        return googleMemberResponse.toEntity(googleAccessTokenByCode.refreshToken());
    }

    @Override
    public void deleteAccount(String refreshToken) {
        GoogleAccessTokenByRefreshToken googleAccessTokenByRefreshToken = googleApiClient.refreshToken("refresh_token",
                googleOauthConfig.clientId(), googleOauthConfig.clientSecret(), refreshToken);
        log.info(googleAccessTokenByRefreshToken.toString());

        googleApiClient.deleteAccount(googleAccessTokenByRefreshToken.accessToken());
    }
}
