package com.enndfp.exception;

import com.enndfp.common.ErrorCode;

/**
 * 自定义异常处理类
 *
 * @author Enndfp
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -2917110114945629349L;
    private final int code;

    public BusinessException(String message, int code) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
