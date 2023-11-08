package com.enndfp.vo;

import lombok.Data;

/**
 * 我的粉丝视图
 *
 * @author Enndfp
 */
@Data
public class FansVO {
    /**
     * 粉丝id
     */
    private String fanId;

    /**
     * 粉丝昵称
     */
    private String nickname;

    /**
     * 粉丝头像
     */
    private String face;

    /**
     * 是否互相关注
     */
    private boolean isFriend = false;
}