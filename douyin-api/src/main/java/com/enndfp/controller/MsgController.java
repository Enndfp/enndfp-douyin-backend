package com.enndfp.controller;

import com.enndfp.common.BaseResponse;
import com.enndfp.common.ErrorCode;
import com.enndfp.message.MessageQueryRequest;
import com.enndfp.mo.MessageMO;
import com.enndfp.service.MessageService;
import com.enndfp.utils.ResultUtils;
import com.enndfp.utils.ThrowUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Enndfp
 */
@Slf4j
@Api(tags = "消息功能接口")
@RestController
@RequestMapping("/msg")
public class MsgController {

    @Resource
    private MessageService messageService;

    @GetMapping("/list")
    public BaseResponse<List<MessageMO>> list(@ModelAttribute MessageQueryRequest messageQueryRequest) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(messageQueryRequest == null, ErrorCode.PARAMS_ERROR);

        // 2. 执行分页查询消息
        List<MessageMO> messageMOList = messageService.queryList(messageQueryRequest);

        return ResultUtils.success(messageMOList);
    }
}
