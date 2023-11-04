package com.enndfp.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 *
 * @author Enndfp
 */
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 3269541181052781710L;

    // 响应状态码
    private Integer code;

    // 响应数据
    private T data;

    // 响应消息
    private String message;

    public BaseResponse(Integer code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(Integer code, T data) {
        this(code,data,"");
    }

    public BaseResponse(Integer code, String message) {
        this(code,null,message);
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }

}
