package com.ssafy.ssapay.global.error.type;

public class SsapayException extends RuntimeException {

    public SsapayException(String message) {
        super(message);
    }

    public SsapayException(String message, Exception e) {
        super(message, e);
    }
}
