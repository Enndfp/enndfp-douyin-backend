package com.enndfp.vo;

import lombok.Data;

import java.util.Date;

/**
 * 评论视图
 *
 * @author Enndfp
 */
@Data
public class CommentVO {

    /**
     * 评论id
     */
    private Long commentId;

    /**
     * 评论者id
     */
    private Long commentUserId;

    /**
     * 评论者昵称
     */
    private String commentUserNickname;

    /**
     * 评论者头像
     */
    private String commentUserFace;

    /**
     * 评论的短时频
     */
    private Long vlogId;

    /**
     * 评论的短视频博主id
     */
    private Long vlogerId;

    /**
     * 评论的内容
     */
    private String content;

    /**
     * 如果是回复留言，则本条为子留言，需要关联查询
     */
    private Long fatherCommentId;

    /**
     * 评论的点赞数
     */
    private Integer likeCounts;

    /**
     * 被评论用户的昵称
     */
    private String replayedUserNickname;

    /**
     * 评论的发表时间
     */
    private Date createdTime;

    /**
     * 评论是否被用户点赞
     */
    private Integer isLike = 0;
}