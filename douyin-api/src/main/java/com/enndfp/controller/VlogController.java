package com.enndfp.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enndfp.common.BaseResponse;
import com.enndfp.common.ErrorCode;
import com.enndfp.dto.vlog.VlogPublishRequest;
import com.enndfp.dto.vlog.VlogQueryRequest;
import com.enndfp.enums.YesOrNo;
import com.enndfp.pojo.Vlog;
import com.enndfp.service.VlogService;
import com.enndfp.utils.ResultUtils;
import com.enndfp.utils.ThrowUtils;
import com.enndfp.vo.VlogVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Enndfp
 */
@Slf4j
@RestController
@RequestMapping("/vlog")
public class VlogController {

    @Resource
    private VlogService vlogService;

    /**
     * 发布视频，保存入库
     *
     * @param vlogPublishRequest
     * @return
     */
    @PostMapping("/publish")
    public BaseResponse<?> publish(@RequestBody VlogPublishRequest vlogPublishRequest) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(vlogPublishRequest == null, ErrorCode.PARAMS_ERROR);
        Long vlogUserId = vlogPublishRequest.getVlogUserId();
        String url = vlogPublishRequest.getUrl();
        String cover = vlogPublishRequest.getCover();
        String title = vlogPublishRequest.getTitle();
        Integer width = vlogPublishRequest.getWidth();
        Integer height = vlogPublishRequest.getHeight();
        ThrowUtils.throwIf(vlogUserId == null, ErrorCode.VLOG_PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isBlank(url), ErrorCode.VLOG_PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isBlank(cover), ErrorCode.VLOG_PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.VLOG_PARAMS_ERROR);
        ThrowUtils.throwIf(width == null, ErrorCode.VLOG_PARAMS_ERROR);
        ThrowUtils.throwIf(height == null, ErrorCode.VLOG_PARAMS_ERROR);

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
    @GetMapping("/indexList")
    public BaseResponse<Page<VlogVO>> indexList(@ModelAttribute VlogQueryRequest vlogQueryRequest) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(vlogQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 2. 处理分页查询逻辑
        Page<VlogVO> vlogVOPage = vlogService.getIndexVlogList(vlogQueryRequest);

        return ResultUtils.success(vlogVOPage);
    }

    /**
     * 从搜索页点击查看vlog详情
     *
     * @param userId
     * @param vlogId
     * @return
     */
    @GetMapping("/detail")
    public BaseResponse<VlogVO> detail(@RequestParam(defaultValue = "") Long userId,
                                       @RequestParam Long vlogId) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(vlogId == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理查看vlog详情逻辑
        return ResultUtils.success(vlogService.getVlogDetailById(userId, vlogId));
    }

    /**
     * 将视频改为私密
     *
     * @param userId
     * @param vlogId
     * @return
     */
    @PostMapping("changeToPrivate")
    public BaseResponse<?> changeToPrivate(@RequestParam Long userId,
                                           @RequestParam Long vlogId) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(userId == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(vlogId == null, ErrorCode.PARAMS_ERROR);
        // 2. 处理修改逻辑
        vlogService.changeToPrivateOrPublic(userId, vlogId, YesOrNo.YES.type);

        return ResultUtils.success();
    }

    /**
     * 将视频改为私密
     *
     * @param userId
     * @param vlogId
     * @return
     */
    @PostMapping("changeToPublic")
    public BaseResponse<?> changeToPublic(@RequestParam Long userId,
                                          @RequestParam Long vlogId) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(userId == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(vlogId == null, ErrorCode.PARAMS_ERROR);
        // 2. 处理修改逻辑
        vlogService.changeToPrivateOrPublic(userId, vlogId, YesOrNo.NO.type);

        return ResultUtils.success();
    }

    /**
     * 分页查询公开视频
     *
     * @param vlogQueryRequest
     * @return
     */
    @GetMapping("/myPublicList")
    public BaseResponse<Page<Vlog>> myPublicList(@ModelAttribute VlogQueryRequest vlogQueryRequest) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(vlogQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 2. 处理分页查询公开视频逻辑
        Page<Vlog> vlogVOPage = vlogService.queryMyVlogList(vlogQueryRequest,YesOrNo.NO.type);

        return ResultUtils.success(vlogVOPage);
    }

    /**
     * 分页查询私密视频
     *
     * @param vlogQueryRequest
     * @return
     */
    @GetMapping("/myPrivateList")
    public BaseResponse<Page<Vlog>> myPrivateList(@ModelAttribute VlogQueryRequest vlogQueryRequest) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(vlogQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 2. 处理分页查询私密视频逻辑
        Page<Vlog> vlogVOPage = vlogService.queryMyVlogList(vlogQueryRequest,YesOrNo.YES.type);

        return ResultUtils.success(vlogVOPage);
    }


}
