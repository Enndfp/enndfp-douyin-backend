package com.enndfp.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enndfp.common.BaseResponse;
import com.enndfp.common.ErrorCode;
import com.enndfp.dto.vlog.VlogPublishRequest;
import com.enndfp.dto.vlog.VlogQueryRequest;
import com.enndfp.enums.YesOrNo;
import com.enndfp.service.VlogService;
import com.enndfp.utils.ResultUtils;
import com.enndfp.utils.ThrowUtils;
import com.enndfp.vo.VlogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Enndfp
 */
@Slf4j
@Api(tags = "短视频功能接口")
@RestController
@RequestMapping("/vlog")
public class VlogController {

    @Resource
    private VlogService vlogService;

    /**
     * 发布视频
     *
     * @param vlogPublishRequest
     * @return
     */
    @ApiOperation(value = "发布视频")
    @PostMapping("/publish")
    public BaseResponse<?> publish(@RequestBody VlogPublishRequest vlogPublishRequest) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(vlogPublishRequest == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理发布视频逻辑
        vlogService.publish(vlogPublishRequest);

        return ResultUtils.success();
    }

    /**
     * 分页查询视频带搜索功能
     *
     * @param vlogQueryRequest
     * @return
     */
    @ApiOperation(value = "分页查询视频带搜索功能")
    @GetMapping("/indexList")
    public BaseResponse<Page<VlogVO>> indexList(@ModelAttribute VlogQueryRequest vlogQueryRequest) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(vlogQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 2. 处理分页查询逻辑
        Page<VlogVO> vlogVOPage = vlogService.getIndexVlogList(vlogQueryRequest);

        return ResultUtils.success(vlogVOPage);
    }

    /**
     * 分页查询用户关注的博主发布的短视频
     *
     * @param vlogQueryRequest
     * @return
     */
    @ApiOperation(value = "分页查询用户关注的博主发布的短视频")
    @GetMapping("/followList")
    public BaseResponse<Page<VlogVO>> followList(@ModelAttribute VlogQueryRequest vlogQueryRequest) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(vlogQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 2. 处理分页查询逻辑
        Page<VlogVO> vlogVOPage = vlogService.getMyFollowVlogList(vlogQueryRequest);

        return ResultUtils.success(vlogVOPage);
    }

    /**
     * 分页查询用户朋友发布的短视频
     *
     * @param vlogQueryRequest
     * @return
     */
    @ApiOperation(value = "分页查询用户朋友发布的短视频")
    @GetMapping("/friendList")
    public BaseResponse<Page<VlogVO>> friendList(@ModelAttribute VlogQueryRequest vlogQueryRequest) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(vlogQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 2. 处理分页查询逻辑
        Page<VlogVO> vlogVOPage = vlogService.getMyFriendVlogList(vlogQueryRequest);

        return ResultUtils.success(vlogVOPage);
    }

    /**
     * 分页查询用户点赞过的短视频
     *
     * @param vlogQueryRequest
     * @return
     */
    @ApiOperation(value = "分页查询用户点赞过的短视频")
    @GetMapping("/myLikedList")
    public BaseResponse<Page<VlogVO>> myLikedList(@ModelAttribute VlogQueryRequest vlogQueryRequest) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(vlogQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 2. 处理分页查询逻辑
        Page<VlogVO> vlogVOPage = vlogService.getMyLikedVlogList(vlogQueryRequest);

        return ResultUtils.success(vlogVOPage);
    }

    /**
     * 查看vlog详情
     *
     * @param userId
     * @param vlogId
     * @return
     */
    @ApiOperation(value = "查看vlog详情")
    @GetMapping("/detail")
    public BaseResponse<VlogVO> detail(@RequestParam(defaultValue = "") Long userId,
                                       @RequestParam Long vlogId) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(vlogId == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理查看vlog详情逻辑
        return ResultUtils.success(vlogService.getVlogDetailById(userId, vlogId));
    }

    /**
     * 将视频改为公开/私密
     *
     * @param userId
     * @param vlogId
     * @return
     */
    @ApiOperation(value = "将视频改为公开/私密")
    @PostMapping("changePrivacy")
    public BaseResponse<?> changePrivacy(@RequestParam Long userId,
                                         @RequestParam Long vlogId,
                                         @RequestParam Integer isPrivate) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(userId == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(vlogId == null, ErrorCode.PARAMS_ERROR);
        // 2. 处理修改逻辑
        vlogService.changePrivacy(userId, vlogId, isPrivate);

        return ResultUtils.success();
    }

    /**
     * 分页查询公开视频
     *
     * @param vlogQueryRequest
     * @return
     */
    @ApiOperation(value = "分页查询公开视频")
    @GetMapping("/myPublicList")
    public BaseResponse<Page<VlogVO>> myPublicList(@ModelAttribute VlogQueryRequest vlogQueryRequest) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(vlogQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 2. 处理分页查询公开视频逻辑
        Page<VlogVO> vlogVOPage = vlogService.queryMyVlogList(vlogQueryRequest, YesOrNo.NO.type);

        return ResultUtils.success(vlogVOPage);
    }

    /**
     * 分页查询私密视频
     *
     * @param vlogQueryRequest
     * @return
     */
    @ApiOperation(value = "分页查询私密视频")
    @GetMapping("/myPrivateList")
    public BaseResponse<Page<VlogVO>> myPrivateList(@ModelAttribute VlogQueryRequest vlogQueryRequest) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(vlogQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 2. 处理分页查询私密视频逻辑
        Page<VlogVO> vlogVOPage = vlogService.queryMyVlogList(vlogQueryRequest, YesOrNo.YES.type);

        return ResultUtils.success(vlogVOPage);
    }

    /**
     * 点赞视频
     *
     * @param userId
     * @param vlogerId
     * @param vlogId
     * @return
     */
    @ApiOperation(value = "点赞视频")
    @PostMapping("/like")
    public BaseResponse<?> like(@RequestParam Long userId,
                                @RequestParam Long vlogerId,
                                @RequestParam Long vlogId) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(userId == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(vlogerId == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(vlogId == null, ErrorCode.PARAMS_ERROR);
        // 2. 处理点赞逻辑
        vlogService.userLikeVlog(userId, vlogerId, vlogId);

        return ResultUtils.success();
    }

    /**
     * 取消点赞
     *
     * @param userId
     * @param vlogerId
     * @param vlogId
     * @return
     */
    @ApiOperation(value = "取消点赞")
    @PostMapping("/unlike")
    public BaseResponse<?> unlike(@RequestParam Long userId,
                                  @RequestParam Long vlogerId,
                                  @RequestParam Long vlogId) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(userId == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(vlogerId == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(vlogId == null, ErrorCode.PARAMS_ERROR);
        // 2. 处理点赞逻辑
        vlogService.userUnLikeVlog(userId, vlogerId, vlogId);

        return ResultUtils.success();
    }

    /**
     * 获得用户点赞视频的总数
     *
     * @param vlogId
     * @return
     */
    @ApiOperation(value = "获得用户点赞视频的总数")
    @PostMapping("/totalLikedCounts")
    public BaseResponse<Integer> totalLikedCounts(@RequestParam Long vlogId) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(vlogId == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理查询点赞总数逻辑
        return ResultUtils.success(vlogService.getVlogBeLikedCounts(vlogId));
    }

}
