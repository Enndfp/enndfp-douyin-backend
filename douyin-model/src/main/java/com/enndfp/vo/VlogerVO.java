package com.enndfp.vo;

import lombok.Data;

/**
 * 关注的博主视图
 *
 * @author Enndfp
 */
@Data
public class VlogerVO {
    /**
     * 博主id
     */
    private String vlogerId;

    /**
     * 博主昵称
     */
    private String nickname;

    /**
     * 博主头像
     */
    private String face;

    /**
     * 是否关注，默认关注
     */
    private boolean isFollowed = true;
}