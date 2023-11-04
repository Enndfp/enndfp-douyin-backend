package com.enndfp.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author Enndfp
 */
@Data
public class UserLoginRequest implements Serializable {

    private String phone;

    private String smsCode;

    private static final long serialVersionUID = 4586537003425887549L;
}
