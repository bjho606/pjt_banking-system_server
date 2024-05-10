package com.ssafy.dongsanbu.global.error.type;

public class DataNotFoundException extends BadRequestException {

    public DataNotFoundException(String message) {
        super(message);
    }
}
