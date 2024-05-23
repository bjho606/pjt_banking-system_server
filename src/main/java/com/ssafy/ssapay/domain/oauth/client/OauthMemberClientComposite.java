package com.ssafy.ssapay.domain.oauth.client;

import com.ssafy.ssapay.domain.oauth.OauthServerType;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.global.error.type.BadRequestException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OauthMemberClientComposite {

    private final Map<OauthServerType, OauthMemberClient> mapping;

    public OauthMemberClientComposite(Set<OauthMemberClient> clients) {
        mapping = clients.stream()
                .collect(Collectors.toMap(OauthMemberClient::supportServer, Function.identity()));
    }

    public User fetch(OauthServerType oauthServerType, String authCode) {
        return getClient(oauthServerType).fetch(authCode);
    }

    public void deleteAccount(OauthServerType oauthServerType, String refreshToken) {
        getClient(oauthServerType).deleteAccount(refreshToken);
    }

    private OauthMemberClient getClient(OauthServerType oauthServerType) {
        return Optional.ofNullable(mapping.get(oauthServerType))
                .orElseThrow(() -> new BadRequestException("Unsupported oauth server type: " + oauthServerType.name()));
    }
}
