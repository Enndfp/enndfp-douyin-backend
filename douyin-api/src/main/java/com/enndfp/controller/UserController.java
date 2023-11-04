package com.enndfp.controller;

import cn.hutool.core.util.PhoneUtil;
import com.enndfp.common.BaseResponse;
import com.enndfp.common.ErrorCode;
import com.enndfp.config.MinIOConfig;
import com.enndfp.dto.user.UserLoginRequest;
import com.enndfp.dto.user.UserUpdateRequest;
import com.enndfp.dto.user.UserUploadRequest;
import com.enndfp.enums.FileTypeEnum;
import com.enndfp.enums.UpdateParamsEnum;
import com.enndfp.pojo.User;
import com.enndfp.service.UserService;
import com.enndfp.utils.RedisUtils;
import com.enndfp.utils.ResultUtils;
import com.enndfp.utils.ThrowUtils;
import com.enndfp.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.enndfp.constant.RedisConstants.*;

/**
 * @author Enndfp
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 发送手机验证码
     *
     * @param phone
     * @param request
     * @return
     */
    @PostMapping("/getSMSCode")
    public BaseResponse<?> getSMSCode(@RequestParam String phone, HttpServletRequest request) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isBlank(phone), ErrorCode.PHONE_IS_NULL);
        ThrowUtils.throwIf(!PhoneUtil.isPhone(phone), ErrorCode.PHONE_ILLEGALITY);
        // 2. 发送验证码
        userService.sendCode(phone, request);

        return ResultUtils.success();
    }

    /**
     * 用户登录，不存在则注册
     *
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<UserVO> login(@RequestBody UserLoginRequest userLoginRequest) {
        String phone = userLoginRequest.getPhone();
        String smsCode = userLoginRequest.getSmsCode();
        // 1. 校验请求参数
        ThrowUtils.throwIf(StringUtils.isBlank(phone), ErrorCode.PHONE_IS_NULL);
        ThrowUtils.throwIf(!PhoneUtil.isPhone(phone), ErrorCode.PHONE_ILLEGALITY);
        ThrowUtils.throwIf(StringUtils.isBlank(smsCode), ErrorCode.SMS_IS_NULL);
        // 2. 处理登录逻辑
        UserVO userVO = userService.login(userLoginRequest);

        return ResultUtils.success(userVO);

    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout(HttpServletRequest request) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        // 2. 处理注销逻辑
        boolean result = userService.logout(request);

        return ResultUtils.success(result);
    }

    /**
     * 根据主键查询用户信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/query")
    public BaseResponse<UserVO> query(@RequestParam Long userId) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(userId == null, ErrorCode.PARAMS_ERROR);
        // 2. 查询用户信息
        UserVO userVO = userService.queryUserById(userId);

        return ResultUtils.success(userVO);
    }

    /**
     * 修改用户基本信息
     *
     * @param userUpdateRequest
     * @param type
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<UserVO> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, @RequestParam Integer type) {
        // 1. 校验请求参数
        ThrowUtils.throwIf(userUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        UpdateParamsEnum.validUpdateParamType(type);
        // 2. 处理修改用户信息
        UserVO userVO = userService.updateUser(userUpdateRequest, type);

        return ResultUtils.success(userVO);
    }

}
