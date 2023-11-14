package com.enndfp.dto.comment;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;

/**
 * 评论修改请求体
 *
 * @author Enndfp
 */
@Data
public class CommentUpdateRequest implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 评论id
     */
    private Long commentId;

    /**
     * 短视频id
     */
    private Long vlogId;

    private static final long serialVersionUID = 1L;
}