package com.ssafy.ssapay.domain.oauth.service;

import com.ssafy.ssapay.domain.oauth.OauthServerType;
import com.ssafy.ssapay.domain.oauth.authcode.AuthCodeRequestUrlProviderComposite;
import com.ssafy.ssapay.domain.oauth.client.OauthMemberClientComposite;
import com.ssafy.ssapay.domain.oauth.controller.OauthServerTypeConverter;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.infra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OAuthService {
    private final AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;
    private final OauthServerTypeConverter oauthServerTypeConverter;
    private final OauthMemberClientComposite oauthMemberClientComposite;
    private final UserRepository userRepository;

    public String getAuthCodeRequestUrl(String oauthServerType) {
        OauthServerType oauthServer = oauthServerTypeConverter.convert(oauthServerType);
        return authCodeRequestUrlProviderComposite.provide(oauthServer);
    }

    @Transactional
    public User loginOrSignup(String oauthServerType, String authCode) {
        OauthServerType oauthServer = oauthServerTypeConverter.convert(oauthServerType);
        User oauthUser = oauthMemberClientComposite.fetch(oauthServer, authCode);

        User user = userRepository.findByOauthId(oauthUser.getOauthId())
                .orElseGet(() -> userRepository.save(oauthUser));
        log.debug("logined member: {} {}", user.getId(), user.getNickname());

        return user;
    }
}
