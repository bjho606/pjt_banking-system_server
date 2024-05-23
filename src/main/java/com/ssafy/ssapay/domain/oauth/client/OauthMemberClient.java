package com.ssafy.ssapay.domain.oauth.client;


import com.ssafy.ssapay.domain.oauth.OauthServerType;
import com.ssafy.ssapay.domain.user.entity.User;

public interface OauthMemberClient {

    OauthServerType supportServer();

    User fetch(String code);

    void deleteAccount(String refreshToken);
}
