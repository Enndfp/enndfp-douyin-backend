package com.enndfp.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 点赞短视频关系表
 * @TableName my_liked_vlog
 */
@TableName(value ="my_liked_vlog")
@Data
public class MyLikedVlog implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 短视频id
     */
    private Long vlogId;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;

    /**
     * 删除时间
     */
    private Date deletedTime;

    /**
     * 是否删除
     */
    @TableLogic(value = "0", delval = "1,deleted_time = now()")
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}