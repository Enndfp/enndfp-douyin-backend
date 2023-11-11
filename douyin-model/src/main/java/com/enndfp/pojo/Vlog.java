package com.enndfp.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 短视频表
 * @TableName vlog
 */
@TableName(value ="vlog")
@Data
public class Vlog implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * vlog视频发布者
     */
    private Long vlogerId;

    /**
     * 视频播放地址
     */
    private String url;

    /**
     * 视频封面
     */
    private String cover;

    /**
     * 视频标题
     */
    private String title;

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
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}