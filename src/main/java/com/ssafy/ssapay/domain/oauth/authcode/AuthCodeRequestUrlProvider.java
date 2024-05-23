package com.ssafy.ssapay.domain.oauth.authcode;

import com.ssafy.ssapay.domain.oauth.OauthServerType;

public interface AuthCodeRequestUrlProvider {

    OauthServerType supportServer();

    String provide();
}
