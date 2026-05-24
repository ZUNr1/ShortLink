package org.zunr1.backend.utils;

import org.springframework.stereotype.Component;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class ShortCodeGenerator {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int CODE_LENGTH = 6; // 6位短码

    /**
     * 基于长URL + 用户ID + 随机盐 生成短码
     * 无法被预测和遍历
     */
    public String generateShortCode(String longUrl, Long userId) {
        // 加入随机盐和用户ID，防止彩虹表攻击
        String salt = String.valueOf(ThreadLocalRandom.current().nextInt(10000, 99999));
        String input = longUrl + userId + salt + System.currentTimeMillis();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());

            // 取前4个字节（32位）转为整数
            long hashValue = ((long) (digest[0] & 0xFF) << 24) |
                    ((long) (digest[1] & 0xFF) << 16) |
                    ((long) (digest[2] & 0xFF) << 8) |
                    ((long) (digest[3] & 0xFF));

            // 转为6位Base62
            return encode(Math.abs(hashValue));

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    /**
     * 将数值转为Base62字符串（固定长度）
     */
    private String encode(long num) {
        StringBuilder sb = new StringBuilder();
        long value = num;

        for (int i = 0; i < CODE_LENGTH; i++) {
            int remainder = (int) (value % BASE62.length());
            sb.append(BASE62.charAt(remainder));
            value = value / BASE62.length();
        }

        return sb.toString();
    }

    /**
     * 生成带混淆的短码（更安全）
     * 添加自定义混淆表，防止被解码
     */
    public String generateSecureCode(String longUrl, Long userId) {
        String basicCode = generateShortCode(longUrl, userId);
        // 字符替换混淆
        return obfuscate(basicCode);
    }

    private String obfuscate(String code) {
        // 自定义映射表，增加破解难度
        char[] chars = code.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            switch (chars[i]) {
                case '0': chars[i] = 'K'; break;
                case '1': chars[i] = 'X'; break;
                case '2': chars[i] = 'Z'; break;
                case '3': chars[i] = 'M'; break;
                case '4': chars[i] = 'Q'; break;
                case '5': chars[i] = 'V'; break;
                case '6': chars[i] = 'W'; break;
                case '7': chars[i] = 'R'; break;
                case '8': chars[i] = 'T'; break;
                case '9': chars[i] = 'Y'; break;
                default: break;
            }
        }
        return new String(chars);
    }
}