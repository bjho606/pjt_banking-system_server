package com.ssafy.ssapay.global.util;

import java.util.Random;

public final class CommonUtil {
    private static final Random random = new Random();

    private CommonUtil() {
    }

    public static void generateRandomException() {
        int num = random.nextInt(50) + 1;
        if (num == 1) {
            throw new RuntimeException("Error occured!");
        }
    }
}
