package com.enndfp.utils;


import com.enndfp.common.ErrorCode;
import com.enndfp.exception.BusinessException;

/**
 * 抛异常工具类
 *
 * @author Enndfp
 */
public class ThrowUtils {

    /**
     * 无条件抛出业务异常
     *
     * @param errorCode 业务错误码
     */
    public static void throwException(ErrorCode errorCode) {
        throw new BusinessException(errorCode);
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param runtimeException
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param errorCode
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param errorCode
     * @param message
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }
}
