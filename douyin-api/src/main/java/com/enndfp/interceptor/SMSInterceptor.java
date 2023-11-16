package com.enndfp.interceptor;

import com.enndfp.common.ErrorCode;
import com.enndfp.utils.IPUtils;
import com.enndfp.utils.RedisUtils;
import com.enndfp.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.enndfp.constant.RedisConstants.LOGIN_IP_KEY;

/**
 * 验证码 60s 拦截器
 *
 * @author Enndfp
 */
@Slf4j
public class SMSInterceptor implements HandlerInterceptor {

    private final RedisUtils redisUtils;

    public SMSInterceptor(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 获得用户的IP
        String userIp = IPUtils.getRequestIp(request);

        // 2. 判断 Redis 中是否存在，存在说明 60s 内用户再次请求则拦截
        boolean keyIsExist = redisUtils.keyIsExist(LOGIN_IP_KEY + userIp);
        if (keyIsExist) {
            ThrowUtils.throwException(ErrorCode.SMS_NEED_WAIT);
            log.info("用户 60s 内再次请求发送验证码");
            return false;
        }
        return true;
    }
}
