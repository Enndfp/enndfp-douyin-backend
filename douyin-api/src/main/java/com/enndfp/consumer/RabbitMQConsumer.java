package com.enndfp.consumer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.enndfp.common.ErrorCode;
import com.enndfp.constant.RabbitMQConstants;
import com.enndfp.enums.MessageEnum;
import com.enndfp.mo.MessageMO;
import com.enndfp.pojo.Comment;
import com.enndfp.pojo.Vlog;
import com.enndfp.service.CommentService;
import com.enndfp.service.MessageService;
import com.enndfp.service.VlogService;
import com.enndfp.utils.ThrowUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Enndfp
 */
@Component
public class RabbitMQConsumer {

    @Resource
    private MessageService messageService;
    @Resource
    private CommentService commentService;
    @Resource
    private VlogService vlogService;

    /**
     * 系统消息消费者
     *
     * @param messageMO
     * @param message
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConstants.SYS_MSG_QUEUE, durable = "true"),
            exchange = @Exchange(name = RabbitMQConstants.SYS_MSG_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = RabbitMQConstants.SYS_MSG_ROUTING_KEY
    ))
    public void sysMsgConsumer(MessageMO messageMO, Message message) {
        // 1. 获取routingKey
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        // 2. 根据不同的routingKey发送不同的系统消息
        if (RabbitMQConstants.SYS_MSG_FOLLOW_ROUTING_KEY.equals(routingKey)) {
            // 2.1 发送关注消息
            messageService.createMsg(
                    messageMO.getFromUserId(),
                    messageMO.getToUserId(),
                    MessageEnum.FOLLOW_YOU.type,
                    null);
        } else if (RabbitMQConstants.SYS_MSG_LIKE_VLOG_ROUTING_KEY.equals(routingKey)) {
            // 2.2 发送喜欢视频的消息
            messageService.createMsg(
                    messageMO.getFromUserId(),
                    messageMO.getToUserId(),
                    MessageEnum.LIKE_VLOG.type,
                    messageMO.getMsgContent());
        } else if (RabbitMQConstants.SYS_MSG_COMMENT_ROUTING_KEY.equals(routingKey)) {
            // 2.3 发送发布评论的消息
            messageService.createMsg(
                    messageMO.getFromUserId(),
                    messageMO.getToUserId(),
                    MessageEnum.COMMENT_VLOG.type,
                    messageMO.getMsgContent());
        } else if (RabbitMQConstants.SYS_MSG_REPLAY_ROUTING_KEY.equals(routingKey)) {
            // 2.4 发送回复评论的消息
            messageService.createMsg(
                    messageMO.getFromUserId(),
                    messageMO.getToUserId(),
                    MessageEnum.REPLAY_YOU.type,
                    messageMO.getMsgContent());
        } else if (RabbitMQConstants.SYS_MSG_LIKE_COMMENT_ROUTING_KEY.equals(routingKey)) {
            // 2.5 发送喜欢评论的消息
            messageService.createMsg(
                    messageMO.getFromUserId(),
                    messageMO.getToUserId(),
                    MessageEnum.LIKE_COMMENT.type,
                    messageMO.getMsgContent());
        } else {
            ThrowUtils.throwException(ErrorCode.MQ_MSG_SEND_FAILED);
        }

    }

    /**
     * 评论消费者
     *
     * @param map
     * @param message
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConstants.COMMENT_QUEUE, durable = "true"),
            exchange = @Exchange(name = RabbitMQConstants.COMMENT_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = RabbitMQConstants.COMMENT_ROUTING_KEY
    ))
    public void commentLikeCount(Map<String, Object> map, Message message) {
        // 1. 获取routingKey
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        // 2. 根据不同的routingKey判断是进行如何操作
        if (RabbitMQConstants.COMMENT_INC_ROUTING_KEY.equals(routingKey)) {
            // 2.1 数据库点赞数 +1
            LambdaUpdateWrapper<Comment> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Comment::getId, map.get("commentId"));
            updateWrapper.setSql("like_counts = like_counts + 1");
            boolean result = commentService.update(null, updateWrapper);
            ThrowUtils.throwIf(!result, ErrorCode.LIKE_FAILED);
        } else if (RabbitMQConstants.COMMENT_DEC_ROUTING_KEY.equals(routingKey)) {
            // 2.2 数据库点赞数 -1
            LambdaUpdateWrapper<Comment> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Comment::getId, map.get("commentId"));
            updateWrapper.setSql("like_counts = like_counts - 1");
            boolean result = commentService.update(null, updateWrapper);
            ThrowUtils.throwIf(!result, ErrorCode.LIKE_FAILED);
        } else if (RabbitMQConstants.COMMENT_PUBLISH_ROUTING_KEY.equals(routingKey)) {
            // 2.3 vlog中的评论数 +1
            LambdaUpdateWrapper<Vlog> updateWrapper2 = new LambdaUpdateWrapper<>();
            updateWrapper2.eq(Vlog::getId, map.get("vlogId"));
            updateWrapper2.setSql("comments_counts = comments_counts + 1");
            boolean result = vlogService.update(null, updateWrapper2);
            ThrowUtils.throwIf(!result, ErrorCode.COMMENT_PUBLISH_FAILED);
        } else if (RabbitMQConstants.COMMENT_DELETE_ROUTING_KEY.equals(routingKey)) {
            // 2.4 vlog中的评论数 -1
            LambdaUpdateWrapper<Vlog> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Vlog::getId, map.get("vlogId"));
            updateWrapper.setSql("comments_counts = comments_counts - 1");
            boolean result = vlogService.update(updateWrapper);
            ThrowUtils.throwIf(!result, ErrorCode.COMMENT_DELETE_FAILED);
        } else {
            ThrowUtils.throwException(ErrorCode.MQ_MSG_SEND_FAILED);
        }
    }
}
