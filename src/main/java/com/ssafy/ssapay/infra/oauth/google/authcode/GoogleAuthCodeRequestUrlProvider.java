package com.ssafy.ssapay.infra.oauth.google.authcode;

import com.ssafy.ssapay.domain.oauth.OauthServerType;
import com.ssafy.ssapay.domain.oauth.authcode.AuthCodeRequestUrlProvider;
import com.ssafy.ssapay.infra.oauth.google.config.GoogleOauthConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class GoogleAuthCodeRequestUrlProvider implements AuthCodeRequestUrlProvider {

    private final GoogleOauthConfig googleOauthConfig;

    @Override
    public OauthServerType supportServer() {
        return OauthServerType.GOOGLE;
    }

    @Override
    public String provide() {
        return UriComponentsBuilder
                .fromUriString("https://accounts.google.com/o/oauth2/auth")
                .queryParam("response_type", "code")
                .queryParam("client_id", googleOauthConfig.clientId())
                .queryParam("redirect_uri", googleOauthConfig.redirectUri())
                .queryParam("scope", String.join("+", googleOauthConfig.scope()))
                .toUriString();
    }
}
