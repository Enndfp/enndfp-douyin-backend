package com.enndfp.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户修改请求体
 *
 * @author Enndfp
 */
@Data
public class UserUpdateRequest implements Serializable {

    private Long id;

    private String nickname;

    private String douyinNum;

    private String face;

    private Integer sex;

    private Date birthday;

    private String country;

    private String province;

    private String city;

    private String district;

    private String description;

    private String bgImg;

    private Integer douyinUpdateAllowed;

    private static final long serialVersionUID = 1L;
}