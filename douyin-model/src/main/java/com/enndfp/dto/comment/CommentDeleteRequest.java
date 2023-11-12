package com.enndfp.dto.comment;

import lombok.Data;

import java.io.Serializable;

/**
 * 评论删除请求体
 *
 * @author Enndfp
 */
@Data
public class CommentDeleteRequest implements Serializable {

    /**
     * 评论的id
     */
    private Long commentId;

    /**
     * 评论者的id
     */
    private Long commentUserId;

    /**
     * 评论对应短视频的id
     */
    private Long vlogId;

    private static final long serialVersionUID = 1L;
}