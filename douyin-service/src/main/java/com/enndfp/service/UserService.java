package com.enndfp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enndfp.dto.user.UserLoginRequest;
import com.enndfp.dto.user.UserUpdateRequest;
import com.enndfp.dto.user.UserUploadRequest;
import com.enndfp.pojo.User;
import com.enndfp.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Enndfp
 */
public interface UserService extends IService<User> {

    /**
     * 发送手机验证码
     *
     * @param phone
     * @param request
     */
    void sendCode(String phone, HttpServletRequest request);

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @return
     */
    UserVO login(UserLoginRequest userLoginRequest);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean logout(HttpServletRequest request);

    /**
     * 根据手机号查询用户是否存在
     *
     * @param phone
     * @return
     */
    User getUserByPhone(String phone);

    /**
     * 通过手机号创建用户
     *
     * @param phone
     * @return
     */
    User createUser(String phone);

    /**
     * 根据主键查询用户信息
     *
     * @param userId
     * @return
     */
    UserVO queryUserById(Long userId);

    /**
     * 修改用户信息
     *
     * @param userUpdateRequest
     * @return
     */
    UserVO updateUser(UserUpdateRequest userUpdateRequest, Integer type);

    /**
     * 上传头像和背景图
     *
     * @param userUploadRequest
     * @param imgUrl
     * @return
     */
    UserVO upload(UserUploadRequest userUploadRequest, String imgUrl);

}
