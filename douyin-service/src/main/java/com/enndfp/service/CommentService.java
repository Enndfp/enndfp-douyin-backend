package com.enndfp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enndfp.dto.comment.CommentDeleteRequest;
import com.enndfp.dto.comment.CommentPublishRequest;
import com.enndfp.dto.comment.CommentQueryRequest;
import com.enndfp.dto.comment.CommentUpdateRequest;
import com.enndfp.pojo.Comment;
import com.enndfp.vo.CommentVO;

/**
 * @author Enndfp
 */
public interface CommentService extends IService<Comment> {

    /**
     * 发表评论
     *
     * @param commentPublishRequest
     * @return
     */
    CommentVO publish(CommentPublishRequest commentPublishRequest);

    /**
     * 查询评论数
     *
     * @param vlogId
     * @return
     */
    Integer counts(Long vlogId);

    /**
     * 分页查询评论列表
     *
     * @param commentQueryRequest
     * @return
     */
    Page<CommentVO> queryCommentList(CommentQueryRequest commentQueryRequest);

    /**
     * 删除评论
     *
     * @param commentDeleteRequest
     */
    void delete(CommentDeleteRequest commentDeleteRequest);

    /**
     * 点赞
     *
     * @param commentUpdateRequest
     */
    void like(CommentUpdateRequest commentUpdateRequest);

    /**
     * 取消点赞
     *
     * @param commentUpdateRequest
     */
    void unlike(CommentUpdateRequest commentUpdateRequest);
}
