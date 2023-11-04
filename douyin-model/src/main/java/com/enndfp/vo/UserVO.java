package com.enndfp.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户视图
 *
 * @author Enndfp
 */
@Data
public class UserVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 抖音号，唯一标识，需要限制修改次数，可以用于付费修改
     */
    private String douyinNum;

    /**
     * 头像
     */
    private String face;

    /**
     * 性别 1:男  0:女  2:保密
     */
    private Integer sex;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 个人简介
     */
    private String description;

    /**
     * 个人介绍的背景图
     */
    private String bgImg;

    /**
     * 抖音能否被修改，1：默认，可以修改；0，无法修改
     */
    private Integer douyinUpdateAllowed;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;

    /**
     * 用户 token
     */
    private String userToken;

    /**
     * 用户关注的数量
     */
    private Integer myFollowsCounts;

    /**
     * 用户粉丝的数量
     */
    private Integer myFansCounts;

    /**
     * 总获赞数（视频+评论）
     */
    private Integer totalLikeMeCounts;

    private static final long serialVersionUID = 1L;
}