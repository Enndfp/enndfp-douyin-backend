package com.enndfp.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 短视频视图
 *
 * @author Enndfp
 */
@Data
public class VlogVO implements Serializable {

    /**
     * id
     */
    private Long vlogId;

    /**
     * vlog视频发布者
     */
    private Long vlogerId;

    /**
     * vlog作者头像
     */
    private String vlogerFace;

    /**
     * vlog作者昵称
     */
    private String vlogerName;

    /**
     * vlog内容
     */
    private String content;

    /**
     * 视频播放地址
     */
    private String url;

    /**
     * 视频封面
     */
    private String cover;

    /**
     * 视频width
     */
    private Integer width;

    /**
     * 视频height
     */
    private Integer height;

    /**
     * 点赞总数
     */
    private Integer likeCounts;

    /**
     * 评论总数
     */
    private Integer commentsCounts;

    /**
     * 是否私密 0：不私密 1：私密
     */
    private Integer isPrivate;

    /**
     * 视频默认不播放
     */
    private Boolean isPlay = false;

    /**
     * 是否关注vlog作者
     */
    private Boolean doIFollowVloger = false;

    /**
     * 是否点赞这个vlog
     */
    private Boolean doILikeThisVlog = false;

    private static final long serialVersionUID = 1L;
}