package com.ssafy.ssapay.domain.oauth.controller;

import com.ssafy.ssapay.domain.oauth.OauthServerType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OauthServerTypeConverter implements Converter<String, OauthServerType> {

    @Override
    public OauthServerType convert(String source) {
        return OauthServerType.fromName(source);
    }
}
