package com.enndfp.interceptor;

import com.enndfp.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.enndfp.constant.RedisConstants.LOGIN_TOKEN_KEY;
import static com.enndfp.constant.RedisConstants.LOGIN_TOKEN_TTL;

/**
 * 刷新 token 拦截器
 *
 * @author Enndfp
 */
public class RefreshTokenInterceptor implements HandlerInterceptor {

    private RedisUtils redisUtils;

    public RefreshTokenInterceptor(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从 request中取出token
        String token = request.getHeader("authorization");
        if (StringUtils.isBlank(token)) return true;

        // 2. 不为空，则刷新token有效期
        String token_key = LOGIN_TOKEN_KEY + token;
        redisUtils.expire(token_key, LOGIN_TOKEN_TTL);

        return true;
    }
}
