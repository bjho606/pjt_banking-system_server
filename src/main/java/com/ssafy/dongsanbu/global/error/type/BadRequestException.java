package com.ssafy.dongsanbu.global.error.type;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Exception e) {
        super(message, e);
    }
}
