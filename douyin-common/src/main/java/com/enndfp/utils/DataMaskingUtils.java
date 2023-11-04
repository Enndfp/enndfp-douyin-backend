package com.enndfp.utils;

/**
 * 通用脱敏工具类
 *
 * @author Enndfp
 */
public class DataMaskingUtils {

    private static final int MASK_SIZE = 6;
    private static final String MASK_SYMBOL = "*";

    /**
     * 通用脱敏方法
     *
     * @param data 待脱敏的数据
     * @return 脱敏后的数据
     */
    public static String maskData(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }

        int dataLength = data.length();

        if (dataLength <= 2) {
            if (dataLength == 1) {
                return MASK_SYMBOL;
            }
            return MASK_SYMBOL + data.charAt(dataLength - 1);
        }

        int visibleChars = (dataLength - MASK_SIZE) / 2;
        if (visibleChars > 1) {
            return data.substring(0, visibleChars) + repeatString(MASK_SYMBOL, MASK_SIZE) + data.substring(dataLength - visibleChars);
        } else {
            return data.charAt(0) + repeatString(MASK_SYMBOL, dataLength - 2) + data.charAt(dataLength - 1);
        }
    }

    /**
     * 重复字符串的方法
     *
     * @param str   待重复的字符串
     * @param times 重复的次数
     * @return 重复后的字符串
     */
    private static String repeatString(String str, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}
