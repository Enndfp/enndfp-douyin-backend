package com.enndfp.constant;

/**
 * @author Enndfp
 */
public class RedisConstants {

    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 60 * 5L; // 5 分钟

    public static final String LOGIN_TOKEN_KEY = "login:token:";
    public static final Long LOGIN_TOKEN_TTL = 60 * 60 * 24L; // 一天

    public static final String LOGIN_IP_KEY = "login:ip:";
    public static final Long LOGIN_IP_TTL = 60L;

    // 我的关注总数
    public static final String MY_FOLLOWS_COUNTS_KEY = "my:follows_counts:";
    // 我的粉丝总数
    public static final String MY_FANS_COUNTS_KEY = "my:fans_counts:";
    // 博主和粉丝的关联关系，用户判断他们是否互相关注
    public static final String FANS_AND_VLOGER_RELATIONSHIP_KEY = "fans_and_vloger_relationship:";


    // 视频获赞数
    public static final String VLOG_LIKE_COUNTS_KEY = "vlog:like_counts:";
    // 发布者获赞数
    public static final String VLOGER_LIKE_COUNTS_KEY = "vloger:like_counts:";


}
