package top.beforedawn.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64编码解码工具类
 *
 * @author moyulingjiu
 */
public class Base64Util {
    public static String encode(String plaintext) {
        return Base64.getEncoder().encodeToString(plaintext.getBytes(StandardCharsets.UTF_8));
    }

    public static String decode(String ciphertext) {
        return new String(Base64.getDecoder().decode(ciphertext), StandardCharsets.UTF_8);
    }
}
