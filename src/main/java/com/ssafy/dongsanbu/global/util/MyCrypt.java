package com.ssafy.dongsanbu.global.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class MyCrypt {
    private static final int REPEAT_TIME = 1000;

    private MyCrypt() {
    }

    public static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.isEmpty()) {
            return null;
        }

        byte[] ba = new byte[hex.length() / 2];
        for (int i = 0; i < ba.length; i++) {
            ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return ba;
    }

    public static String byteArrayToHex(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder(ba.length * 2);
        String hexNumber;
        for (byte b : ba) {
            hexNumber = "0" + Integer.toHexString(0xff & b);

            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }
        return sb.toString();
    }

    public static byte[] getSHA256(String source, String salt) {
        byte result[] = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(source.getBytes());
            result = md.digest();
            for (int i = 0; i < REPEAT_TIME; i++) {
                String temp = byteArrayToHex(result) + salt;
                md.update(temp.getBytes());
                result = md.digest();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String makeSalt() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}