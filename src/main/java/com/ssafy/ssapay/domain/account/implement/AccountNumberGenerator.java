package com.ssafy.ssapay.domain.account.implement;

import java.security.SecureRandom;

public class AccountNumberGenerator {
    private final SecureRandom random = new SecureRandom();

    public String generateAccountNumber(String prefix) {
        StringBuilder sb = new StringBuilder(10);
        sb.append(prefix);
        for (int i = 0; i < 8; i++) {
            int digit = random.nextInt(10);
            sb.append(digit);
        }

        return sb.toString();
    }
}
