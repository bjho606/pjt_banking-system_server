package com.ssafy.ssapay.domain.oauth;

import java.util.Locale;

public enum OauthServerType {

    GOOGLE,
    ;

    public static OauthServerType fromName(String type) {
        return OauthServerType.valueOf(type.toUpperCase(Locale.ENGLISH));
    }
}
