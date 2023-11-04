package com.enndfp.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enndfp.common.BaseResponse;
import com.enndfp.common.ErrorCode;
import com.enndfp.dto.vlog.VlogPublishRequest;
import com.enndfp.pojo.Vlog;
import com.enndfp.service.VlogService;
import com.enndfp.utils.ResultUtils;
import com.enndfp.utils.ThrowUtils;
import com.enndfp.vo.UserVO;
import com.enndfp.vo.VlogVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.enndfp.constant.VlogConstants.DEFAULT_PAGE;
import static com.enndfp.constant.VlogConstants.DEFAULT_PAGE_SIZE;

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
     * @param search
     * @return
     */
    @GetMapping("/indexList")
    public BaseResponse<Page<VlogVO>> indexList(@RequestParam(defaultValue = "") String search,
                                                @RequestParam Integer page,
                                                @RequestParam Integer pageSize) {
        if(page == null) page = DEFAULT_PAGE;
        if(pageSize ==null) pageSize = DEFAULT_PAGE_SIZE;

        Page<Vlog> vlogPage = vlogService.page(new Page<>(page, pageSize), vlogService.getQueryWrapper(search));

        return ResultUtils.success(vlogVOList);
    }
}
