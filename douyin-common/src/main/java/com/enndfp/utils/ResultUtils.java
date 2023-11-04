package com.enndfp.utils;

import com.enndfp.common.BaseResponse;
import com.enndfp.common.ErrorCode;

import java.util.Map;

/**
 * 返回工具类
 *
 * @author Enndfp
 */
public class ResultUtils {

    /**
     * 返回成功，不带有数据
     *
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(200, null, "操作成功！");
    }

    /**
     * 返回成功，带有数据
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(200, data, "操作成功！");
    }

    /**
     * 返回失败枚举
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @return
     */
    public static BaseResponse error(int code, String message) {
        return new BaseResponse(code, null, message);
    }

    /**
     * 返回自定义失败信息失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String message) {
        return new BaseResponse(errorCode.getCode(), null, message);
    }

    /**
     * 返回自定义失败信息失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, Map map) {
        return new BaseResponse(errorCode.getCode(), map, errorCode.getMessage());
    }
}
