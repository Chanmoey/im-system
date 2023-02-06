package com.moon.im.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author Chanmoey
 * @date 2023年02月04日
 */
public class Base64URL {

    private Base64URL() {
    }

    public static byte[] base64EncodeUrl(byte[] input) {
        byte[] base64 = Base64.getEncoder().encode(input);
        charEncode(base64);
        return base64;
    }

    public static byte[] base64EncodeUrlNotReplace(byte[] input) {
        byte[] base64 = Base64.getEncoder().encode(input);
        charEncode(base64);
        return base64;
    }

    public static byte[] base64DecodeUrlNotReplace(byte[] input) {
        charDecode(input);
        return Base64.getDecoder().decode(new String(input, StandardCharsets.UTF_8));
    }

    public static byte[] base64DecodeUrl(byte[] input) {
        byte[] base64 = input.clone();
        charDecode(base64);
        return Base64.getDecoder().decode(Arrays.toString(base64));
    }

    private static void charEncode(byte[] base64) {
        for (int i = 0; i < base64.length; ++i) {
            switch (base64[i]) {
                case '+':
                    base64[i] = '*';
                    break;
                case '/':
                    base64[i] = '-';
                    break;
                case '=':
                    base64[i] = '_';
                    break;
                default:
                    break;
            }
        }
    }

    private static void charDecode(byte[] base64) {
        for (int i = 0; i < base64.length; ++i) {
            switch (base64[i]) {
                case '*':
                    base64[i] = '+';
                    break;
                case '-':
                    base64[i] = '/';
                    break;
                case '_':
                    base64[i] = '=';
                    break;
                default:
                    break;
            }
        }
    }
}
