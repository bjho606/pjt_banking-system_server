package com.ssafy.ssapay.domain.auth.dto.token;

public abstract class Token {
    protected String key;
    protected String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
