package com.scmt.healthy.utils;

import java.security.SecureRandom;
import java.util.Random;

public class RandomUtil {
    private static final String SYMBOLS = "0123456789"; // 纯数字
    //如果需加入字母就改成0123456789abcdefg...........
    private static final Random RANDOM = new SecureRandom();

    /**
     * 获取长度为 6 的随机数字
     *
     * @return 随机数字
     */
    public static String getSixstr() {
        char[] nonceChars = new char[6];  //指定长度，自己可以设置

        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }
        return new String(nonceChars);
    }
}
