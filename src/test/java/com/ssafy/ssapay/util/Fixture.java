package com.ssafy.ssapay.util;

import com.ssafy.ssapay.domain.user.entity.User;

public final class Fixture {

    public static User createUser(String username, String password, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        return user;
    }
}
