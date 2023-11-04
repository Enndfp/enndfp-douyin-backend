package com.enndfp.config;

import com.enndfp.interceptor.RefreshTokenInterceptor;
import com.enndfp.interceptor.SMSInterceptor;
import com.enndfp.utils.RedisUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author Enndfp
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private RedisUtils redisUtils;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new SMSInterceptor(redisUtils))
                .addPathPatterns("/user/getSMSCode")
                .order(0);

        registry.addInterceptor(new RefreshTokenInterceptor(redisUtils))
                .order(1);

    }
}
