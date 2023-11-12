package com.enndfp.dto.comment;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 发表评论请求体
 *
 * @author Enndfp
 */
@Data
public class CommentPublishRequest implements Serializable {

    /**
     * 评论者id
     */
    @NotNull(message = "当前用户信息不正确，请尝试重新登录")
    @Min(value = 1, message = "评论者ID必须是正整数")
    private Long commentUserId;

    /**
     * 评论的视频id
     */
    @NotNull(message = "留言信息不完整")
    @Min(value = 1, message = "评论者ID必须是正整数")
    private Long vlogId;

    /**
     * 评论视频博主的id
     */
    @NotNull(message = "留言信息不完整")
    @Min(value = 1, message = "评论者ID必须是正整数")
    private Long vlogerId;

    /**
     * 评论的内容
     */
    @NotBlank(message = "评论内容不能为空")
    @Length(min = 1, max = 50, message = "评论内容长度必须在1到50字符之间")
    private String content;

    /**
     * 如果是回复留言，则本条为子留言，需要关联查询
     */
    @NotNull(message = "留言信息不完整")
    @Min(value = 0, message = "父留言ID不能为负数")
    private Long fatherCommentId;

    private static final long serialVersionUID = 1L;
}
