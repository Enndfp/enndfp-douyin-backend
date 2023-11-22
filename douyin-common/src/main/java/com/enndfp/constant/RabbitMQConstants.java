package com.enndfp.constant;

import com.enndfp.enums.MessageEnum;

/**
 * RabbitMQ基本信息常量
 *
 * @author Enndfp
 */
public class RabbitMQConstants {

    public static final String SYS_MSG_EXCHANGE = "sys.msg.exchange";
    public static final String COMMENT_EXCHANGE = "comment.exchange";

    public static final String SYS_MSG_QUEUE = "sys.msg.queue";
    public static final String COMMENT_QUEUE = "comment.queue";

    public static final String SYS_MSG_ROUTING_KEY = "sys.msg.*";
    public static final String COMMENT_ROUTING_KEY = "comment.*";
    public static final String COMMENT_INC_ROUTING_KEY = "comment.inc";
    public static final String COMMENT_DEC_ROUTING_KEY = "comment.dec";
    public static final String COMMENT_PUBLISH_ROUTING_KEY = "comment.publish";
    public static final String COMMENT_DELETE_ROUTING_KEY = "comment.delete";

    public static final String SYS_MSG_FOLLOW_ROUTING_KEY = "sys.msg." + MessageEnum.FOLLOW_YOU.enValue;

    public static final String SYS_MSG_LIKE_VLOG_ROUTING_KEY = "sys.msg." + MessageEnum.LIKE_VLOG.enValue;

    public static final String SYS_MSG_COMMENT_ROUTING_KEY = "sys.msg." + MessageEnum.COMMENT_VLOG.enValue;

    public static final String SYS_MSG_REPLAY_ROUTING_KEY = "sys.msg." + MessageEnum.REPLAY_YOU.enValue;

    public static final String SYS_MSG_LIKE_COMMENT_ROUTING_KEY = "sys.msg." + MessageEnum.LIKE_COMMENT.enValue;


}
