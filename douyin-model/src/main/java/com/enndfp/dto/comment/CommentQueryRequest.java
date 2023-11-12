package com.enndfp.dto.comment;

import lombok.Data;

import java.io.Serializable;

/**
 * 评论查询请求体
 *
 * @author Enndfp
 */
@Data
public class CommentQueryRequest implements Serializable {

    private Long vlogId;

    private Long userId;

    private Integer current;

    private Integer pageSize;

    private static final long serialVersionUID = 1L;
}