package com.enndfp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enndfp.common.ErrorCode;
import com.enndfp.dto.user.UserLoginRequest;
import com.enndfp.dto.user.UserUpdateRequest;
import com.enndfp.dto.user.UserUploadRequest;
import com.enndfp.enums.FileTypeEnum;
import com.enndfp.enums.UpdateParamsEnum;
import com.enndfp.enums.YesOrNo;
import com.enndfp.mapper.UserMapper;
import com.enndfp.pojo.User;
import com.enndfp.service.UserService;
import com.enndfp.utils.IPUtils;
import com.enndfp.utils.RedisIdWorker;
import com.enndfp.utils.RedisUtils;
import com.enndfp.utils.ThrowUtils;
import com.enndfp.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.enndfp.constant.RedisConstants.*;
import static com.enndfp.constant.UserConstants.*;

/**
 * @author Enndfp
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RedisIdWorker redisIdWorker;


    @Override
    public void sendCode(String phone, HttpServletRequest request) {
        // 1. 从 request 中获得用户的IP，根据用户IP进行限制，限制60秒之内只能获得一次验证码
        String userIp = IPUtils.getRequestIp(request);
        redisUtils.set(LOGIN_IP_KEY + userIp, userIp, LOGIN_IP_TTL);

        // 2. 随机生成 6 位验证码，并发送短信
        String code = RandomUtil.randomNumbers(6);
        // int responseCode = SMSUtils.sendSMS(phone, code);
        // ThrowUtils.throwIf(responseCode != 200, ErrorCode.SMS_SEND_FAILED);
        log.info("手机验证码发送成功，验证码为：{}", code);

        // 3. 把手机验证码放入 Redis 中，用于后续的验证
        redisUtils.set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL);
    }

    @Override
    public UserVO login(UserLoginRequest userLoginRequest) {
        String phone = userLoginRequest.getPhone();
        String smsCode = userLoginRequest.getSmsCode();

        // 1. 从 Redis 中获取验证码进行校验匹配
        String code = redisUtils.get(LOGIN_CODE_KEY + phone);
        if (StringUtils.isBlank(code) || !code.equals(smsCode)) {
            ThrowUtils.throwException(ErrorCode.SMS_IS_EXPIRED_OR_NOT_MATCH);
        }

        // 2. 如果一致，根据手机号查询
        User user = getUserByPhone(phone);
        // 2.1 判断用户是否存在
        if (user == null) {
            // 2.2 不存在，创建新用户
            user = createUser(phone);
        }

        // 3. 保存用户到redis
        // 3.1 随机生成token，作为登录令牌
        String token = UUID.randomUUID().toString(true);
        // 用户信息脱敏
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        userVO.setUserToken(token);
        // 3.2 将user对象转为hashMap存储
        Map<String, Object> userMap = BeanUtil.beanToMap(userVO, new HashMap<>(), CopyOptions.create()
                .setIgnoreNullValue(true)
                .setFieldValueEditor((fieldName, fieldValue) ->
                        fieldValue != null ? fieldValue.toString() : null));
        // 3.3 存储
        String token_key = LOGIN_TOKEN_KEY + token;
        redisUtils.setHashMap(token_key, userMap);

        // 4. 设置token有效期
        redisUtils.expire(token_key, LOGIN_TOKEN_TTL);

        // 5. 删除 Redis 中的验证码
        redisUtils.del(LOGIN_CODE_KEY + phone);

        // 6. 返回用户信息，包含token令牌
        return userVO;
    }

    @Override
    public boolean logout(HttpServletRequest request) {
        // 1. 取出请求头中的token
        String token = request.getHeader("authorization");

        // 2. 删除 Redis 中的信息
        String token_key = LOGIN_TOKEN_KEY + token;
        redisUtils.del(token_key);

        return true;
    }

    @Override
    public User getUserByPhone(String phone) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        return userMapper.selectOne(queryWrapper);
    }

    @Transactional
    @Override
    public User createUser(String phone) {
        // 1. 获得全局唯一主键
        long userId = redisIdWorker.nextId("user");

        // 2. 填充用户基本信息
        User user = new User();
        user.setId(userId);
        user.setPhone(phone);
        user.setFace(FACE_URL);
        user.setDescription(DEFAULT_DESCRIPTION);
        user.setBirthday(DateUtil.parse(DEFAULT_BIRTHDAY));
        user.setNickname(NICKNAME_PREFIX + RandomUtil.randomString(8));
        user.setDouyinNum(DOUYIN_NUM_PREFIX + RandomUtil.randomNumbers(8));

        // 3. 保存用户
        userMapper.insert(user);
        return user;
    }

    @Override
    public UserVO queryUserById(Long userId) {
        // 1. 查询用户基本信息
        User user = userMapper.selectById(userId);
        ThrowUtils.throwIf(user == null, ErrorCode.USER_NOT_EXIST);
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);

        // 2. 从 Redis 中查询个人信息页面关注，粉丝和获赞数
        int myFollowsCounts = getIntFromRedis(MY_FOLLOWS_COUNTS_KEY + userId);
        int myFansCounts = getIntFromRedis(MY_FANS_COUNTS_KEY + userId);
        int likedVlogCounts = getIntFromRedis(VLOG_LIKE_COUNTS_KEY + userId);
        int likedVlogerCounts = getIntFromRedis(VLOG_USER_LIKE_COUNTS_KEY + userId);

        // 3. 计算总获赞数（视频+评论）
        int totalLikeMeCounts = likedVlogCounts + likedVlogerCounts;

        // 4. 填充信息
        userVO.setMyFollowsCounts(myFollowsCounts);
        userVO.setMyFansCounts(myFansCounts);
        userVO.setTotalLikeMeCounts(totalLikeMeCounts);

        return userVO;
    }

    @Transactional
    @Override
    public UserVO updateUser(UserUpdateRequest userUpdateRequest, Integer type) {
        // TODO 优化修改用户信息功能，并且修改redis值
        // 1. 如果type等于DOUYINNUM，判断是否唯一并且可以修改
        if (type != null && type.equals(UpdateParamsEnum.DOUYINNUM.type)) {
            // 1.1 构造查询条件
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getDouyinNum, userUpdateRequest.getDouyinNum());
            User user = userMapper.selectOne(queryWrapper);

            // 1.2 判断 DOUYINNUM 是否存在，不为空说明此 DOUYINNUM 已存在，则不能修改
            ThrowUtils.throwIf(user != null, ErrorCode.DOUYINNUM_IS_EXIST);

            // 1.3 DOUYINNUM 不存在，则查询此用户是否有修改次数
            User tempUser = userMapper.selectById(userUpdateRequest.getId());

            // 1.4 判断能否修改次数的值，若为 0 则不能修改
            if (YesOrNo.NO.type.equals(tempUser.getDouyinUpdateAllowed())) {
                ThrowUtils.throwException(ErrorCode.DOUYINNUM_NOT_UPDATE);
            }
            // 1.5 将是否能修改字段的值置为 0
            userUpdateRequest.setDouyinUpdateAllowed(YesOrNo.NO.type);
        }

        // 2. 拷贝user对象
        User user = BeanUtil.copyProperties(userUpdateRequest, User.class);
        int result = userMapper.updateById(user);
        // 2.1 修改失败则报错
        ThrowUtils.throwIf(result != 1, ErrorCode.USER_INFO_UPDATE_ERROR);

        // 3. 查询出修改后的用户信息并封装返回
        User newUser = userMapper.selectById(user.getId());

        return BeanUtil.copyProperties(newUser, UserVO.class);
    }

    @Transactional
    @Override
    public UserVO upload(UserUploadRequest userUploadRequest, String imgUrl) {
        // TODO 修改redis值，可以从redis返回
        // 1. 构造保存用户对象
        Long userId = userUploadRequest.getUserId();
        Integer type = userUploadRequest.getType();
        User user = new User();
        user.setId(userId);
        if (FileTypeEnum.BGIMG.type.equals(type)) {
            user.setBgImg(imgUrl);
        } else {
            user.setFace(imgUrl);
        }
        // 2. 保存到数据库
        int result = userMapper.updateById(user);
        // 3. 保存失败则报错
        ThrowUtils.throwIf(result != 1, ErrorCode.USER_INFO_UPDATE_ERROR);

        // 4. 查询出修改后的用户信息并封装返回
        User newUser = userMapper.selectById(user.getId());

        return BeanUtil.copyProperties(newUser, UserVO.class);
    }

    private int getIntFromRedis(String key) {
        String valueStr = redisUtils.get(key);
        return StringUtils.isNotBlank(valueStr) ? Integer.parseInt(valueStr) : 0;
    }

}




