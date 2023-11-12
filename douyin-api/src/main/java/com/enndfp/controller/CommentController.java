package com.enndfp.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enndfp.common.BaseResponse;
import com.enndfp.common.ErrorCode;
import com.enndfp.dto.comment.CommentDeleteRequest;
import com.enndfp.dto.comment.CommentPublishRequest;
import com.enndfp.dto.comment.CommentQueryRequest;
import com.enndfp.dto.vlog.VlogQueryRequest;
import com.enndfp.enums.YesOrNo;
import com.enndfp.service.CommentService;
import com.enndfp.utils.ResultUtils;
import com.enndfp.utils.ThrowUtils;
import com.enndfp.vo.CommentVO;
import com.enndfp.vo.VlogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author Enndfp
 */
@Slf4j
@Api(tags = "评论功能接口")
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    /**
     * 发表评论
     *
     * @param commentPublishRequest
     * @return
     */
    @ApiOperation(value = "发表评论")
    @PostMapping("/publish")
    public BaseResponse<CommentVO> publish(@Valid @RequestBody CommentPublishRequest commentPublishRequest) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(commentPublishRequest == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理发表评论逻辑
        CommentVO commentVO = commentService.publish(commentPublishRequest);

        return ResultUtils.success(commentVO);
    }

    /**
     * 查询评论数
     *
     * @param vlogId
     * @return
     */
    @ApiOperation(value = "查询评论数")
    @GetMapping("/counts")
    public BaseResponse<Integer> counts(@RequestParam Long vlogId) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(vlogId == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理查询评论数逻辑
        return ResultUtils.success(commentService.counts(vlogId));
    }

    /**
     * 分页查询评论列表
     *
     * @param commentQueryRequest
     * @return
     */
    @ApiOperation(value = "分页查询评论列表")
    @GetMapping("/list")
    public BaseResponse<Page<CommentVO>> list(@ModelAttribute CommentQueryRequest commentQueryRequest) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(commentQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 2. 处理分页查询评论列表逻辑
        Page<CommentVO> commentVOPage = commentService.queryCommentList(commentQueryRequest);

        return ResultUtils.success(commentVOPage);
    }

    /**
     * 删除评论
     *
     * @param commentDeleteRequest
     * @return
     */
    @ApiOperation(value = "删除评论")
    @DeleteMapping("/delete")
    public BaseResponse<?> delete(@ModelAttribute CommentDeleteRequest commentDeleteRequest) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(commentDeleteRequest == null, ErrorCode.PARAMS_ERROR);
        // 2. 处理分页查询评论列表逻辑
        commentService.delete(commentDeleteRequest);

        return ResultUtils.success();
    }
}
