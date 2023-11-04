package com.enndfp.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 粉丝表
 * @TableName fans
 */
@TableName(value ="fans")
@Data
public class Fans implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 喜欢的作者用户id
     */
    private Long followedUserId;

    /**
     * 粉丝用户id
     */
    private Long fanUserId;

    /**
     * 是否互相关注 0:否 1:是
     */
    private Integer isMutualFan;

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