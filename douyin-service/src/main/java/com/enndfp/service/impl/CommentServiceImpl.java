package com.enndfp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enndfp.common.ErrorCode;
import com.enndfp.constant.RabbitMQConstants;
import com.enndfp.dto.comment.CommentDeleteRequest;
import com.enndfp.dto.comment.CommentPublishRequest;
import com.enndfp.dto.comment.CommentQueryRequest;
import com.enndfp.dto.comment.CommentUpdateRequest;
import com.enndfp.enums.MessageEnum;
import com.enndfp.enums.YesOrNo;
import com.enndfp.mapper.CommentMapper;
import com.enndfp.mapper.VlogMapper;
import com.enndfp.mo.MessageMO;
import com.enndfp.pojo.Comment;
import com.enndfp.pojo.Vlog;
import com.enndfp.service.CommentService;
import com.enndfp.service.MessageService;
import com.enndfp.service.UserService;
import com.enndfp.utils.RedisIdWorker;
import com.enndfp.utils.RedisUtils;
import com.enndfp.utils.ThrowUtils;
import com.enndfp.vo.CommentVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.enndfp.constant.RedisConstants.*;
import static com.enndfp.constant.PageConstants.DEFAULT_CURRENT;
import static com.enndfp.constant.PageConstants.DEFAULT_PAGE_SIZE;

/**
 * @author Enndfp
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {

    @Resource
    private CommentMapper commentMapper;
    @Resource
    private RedisIdWorker redisIdWorker;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private UserService userService;
    @Resource
    private VlogMapper vlogMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public CommentVO publish(CommentPublishRequest commentPublishRequest) {
        Long commentUserId = commentPublishRequest.getCommentUserId();
        Long vlogId = commentPublishRequest.getVlogId();
        Long vlogerId = commentPublishRequest.getVlogerId();
        Long fatherCommentId = commentPublishRequest.getFatherCommentId();

        // 1. 判断如果该条评论是子评论，则需要判断父评论是否存在
        if (fatherCommentId != null && fatherCommentId != 0) {
            LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Comment::getId, fatherCommentId);
            Comment comment = commentMapper.selectOne(queryWrapper);
            ThrowUtils.throwIf(comment == null, ErrorCode.COMMENT_IS_NOT_EXIST);
        }

        // 2. 判断评论者和视频博主是否存在
        userService.checkUserExist(Arrays.asList(commentUserId, vlogerId));

        // 3. 拷贝对象，填充基本信息
        Comment comment = BeanUtil.copyProperties(commentPublishRequest, Comment.class);
        long id = redisIdWorker.nextId("comment");
        comment.setId(id);

        // 4. 保存评论
        int result = commentMapper.insert(comment);
        ThrowUtils.throwIf(result != 1, ErrorCode.COMMENT_PUBLISH_FAILED);

        // 5. 在 Redis 中保存该视频的评论总数
        redisUtils.increment(VLOG_COMMENT_COUNTS + commentPublishRequest.getVlogId(), 1);

        // 6. 异步解耦，数据库评论数 + 1
        Map<String, Object> map = new HashMap<>();
        map.put("vlogId", vlogId);
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConstants.COMMENT_EXCHANGE,
                    RabbitMQConstants.COMMENT_PUBLISH_ROUTING_KEY,
                    map);
        } catch (AmqpException e) {
            log.error(e.toString());
        }

        // 7. 异步解耦，用 RabbitMQ 发送系统消息：评论/回复
        HashMap<String, Object> msgContent = new HashMap<>();
        Vlog vlog = vlogMapper.selectById(vlogId);
        msgContent.put("vlogId", vlog.getId());
        msgContent.put("vlogCover", vlog.getCover());
        msgContent.put("commentId", id);
        msgContent.put("commentContent", commentPublishRequest.getContent());

        MessageMO messageMO = new MessageMO();
        messageMO.setFromUserId(commentUserId);
        messageMO.setToUserId(vlogerId);
        messageMO.setMsgContent(msgContent);

        String routingKey = RabbitMQConstants.SYS_MSG_COMMENT_ROUTING_KEY;
        if (fatherCommentId != null && fatherCommentId != 0) {
            routingKey = RabbitMQConstants.SYS_MSG_REPLAY_ROUTING_KEY;
        }
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.SYS_MSG_EXCHANGE,
                routingKey,
                messageMO);

        return BeanUtil.copyProperties(comment, CommentVO.class);
    }

    @Override
    public Integer counts(Long vlogId) {
        // 1. 从 Redis 中取出保存该视频的评论总数
        String countsStr = redisUtils.get(VLOG_COMMENT_COUNTS + vlogId);
        // 2. 如果为空，则设为0
        if (StringUtils.isBlank(countsStr)) {
            countsStr = "0";
        }

        return Integer.valueOf(countsStr);
    }

    @Override
    public Page<CommentVO> queryCommentList(CommentQueryRequest commentQueryRequest) {
        Long vlogId = commentQueryRequest.getVlogId();
        Long userId = commentQueryRequest.getUserId();
        Integer current = commentQueryRequest.getCurrent();
        Integer pageSize = commentQueryRequest.getPageSize();

        // 1. 校验请求参数
        ThrowUtils.throwIf(vlogId == null, ErrorCode.PARAMS_ERROR);

        // 2. 构造分页器
        if (current == null) current = DEFAULT_CURRENT;
        if (pageSize == null) pageSize = DEFAULT_PAGE_SIZE;
        Page<CommentVO> page = new Page<>(current, pageSize);

        // 3. 构造查询条件
        Map<String, Object> map = new HashMap<>();
        map.put("vlogId", vlogId);

        // 4. 处理后置参数
        Page<CommentVO> commentVOPage = commentMapper.getCommentList(page, map);
        // 4.1 得到具体数据
        List<CommentVO> commentVOList = commentVOPage.getRecords();
        for (CommentVO commentVO : commentVOList) {
            // 4.2 判断用户是否点赞该评论
            Long commentId = commentVO.getCommentId();
            Boolean isMember = redisUtils.isMember(COMMENT_LIKED + commentId, userId.toString());
            if (isMember) commentVO.setIsLike(YesOrNo.YES.type);
        }

        return commentVOPage;
    }

    @Override
    public void delete(CommentDeleteRequest commentDeleteRequest) {
        Long commentId = commentDeleteRequest.getCommentId();
        Long commentUserId = commentDeleteRequest.getCommentUserId();
        Long vlogId = commentDeleteRequest.getVlogId();

        // 1. 校验请求参数
        if (commentId == null || commentUserId == null || vlogId == null) {
            ThrowUtils.throwException(ErrorCode.PARAMS_ERROR);
        }

        // 2. 修改 Redis 中评论的总数
        redisUtils.decrement(VLOG_COMMENT_COUNTS + vlogId, 1);

        // 3. 删除comment表中记录
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getId, commentId);
        queryWrapper.eq(Comment::getCommentUserId, commentUserId);
        int result = commentMapper.delete(queryWrapper);
        ThrowUtils.throwIf(result != 1, ErrorCode.COMMENT_DELETE_FAILED);

        // 3. 异步修改vlog表中的评论数
        Map<String, Object> map = new HashMap<>();
        map.put("vlogId", vlogId);
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConstants.COMMENT_EXCHANGE,
                    RabbitMQConstants.COMMENT_DELETE_ROUTING_KEY,
                    map);
        } catch (AmqpException e) {
            log.error(e.toString());
        }
    }

    @Transactional
    @Override
    public void like(CommentUpdateRequest commentUpdateRequest) {
        Long userId = commentUpdateRequest.getUserId();
        Long commentId = commentUpdateRequest.getCommentId();
        Long vlogId = commentUpdateRequest.getVlogId();
        if (userId == null || commentId == null || vlogId == null) {
            ThrowUtils.throwException(ErrorCode.PARAMS_ERROR);
        }

        // 1. 判断当前登录用户是否已经点赞
        Boolean isMember = redisUtils.isMember(COMMENT_LIKED + commentId, userId.toString());
        ThrowUtils.throwIf(BooleanUtil.isTrue(isMember), ErrorCode.ALREADY_LIKE);

        // 2. 保存到 Redis 中
        // 2.1 评论被哪些用户点赞过 set 集合
        redisUtils.sadd(COMMENT_LIKED + commentId, userId.toString());
        // 2.2 评论的点赞总数 +1
        redisUtils.increment(COMMENT_LIKED_COUNTS + commentId, 1);

        // 3. 异步解耦，数据库点赞数 +1
        Map<String, Object> map = new HashMap<>();
        map.put("commentId", commentId);
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConstants.COMMENT_EXCHANGE,
                    RabbitMQConstants.COMMENT_INC_ROUTING_KEY,
                    map);
        } catch (AmqpException e) {
            log.error(e.toString());
        }

        // 4. 异步解耦，用 RabbitMQ 发送系统消息：点赞评论
        Map<String, Object> msgContent = new HashMap<>();
        Comment comment = this.getById(commentId);
        Vlog vlog = vlogMapper.selectById(vlogId);
        msgContent.put("vlogId", vlogId);
        msgContent.put("vlogCover", vlog.getCover());
        msgContent.put("commentId", commentId);

        MessageMO messageMO = new MessageMO();
        messageMO.setFromUserId(userId);
        messageMO.setToUserId(comment.getCommentUserId());
        messageMO.setMsgContent(msgContent);

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConstants.SYS_MSG_EXCHANGE,
                    RabbitMQConstants.SYS_MSG_LIKE_COMMENT_ROUTING_KEY,
                    messageMO);
        } catch (AmqpException e) {
            log.error(e.toString());
        }
    }

    @Transactional
    @Override
    public void unlike(CommentUpdateRequest commentUpdateRequest) {
        Long userId = commentUpdateRequest.getUserId();
        Long commentId = commentUpdateRequest.getCommentId();
        Long vlogId = commentUpdateRequest.getVlogId();
        if (userId == null || commentId == null || vlogId == null) {
            ThrowUtils.throwException(ErrorCode.PARAMS_ERROR);
        }

        // 1. 判断当前登录用户是否已经点赞
        Boolean isMember = redisUtils.isMember(COMMENT_LIKED + commentId, userId.toString());
        ThrowUtils.throwIf(BooleanUtil.isFalse(isMember), ErrorCode.UN_LIKE);

        // 2. 修改 Redis 中的记录
        // 2.1 删除用户点赞的记录
        redisUtils.sdel(COMMENT_LIKED + commentId, userId.toString());
        // 2.2 评论的点赞总数 -1
        redisUtils.decrement(COMMENT_LIKED_COUNTS + commentId, 1);

        // 3. 异步解耦，数据库点赞数 -1
        Map<String, Object> map = new HashMap<>();
        map.put("commentId", commentId);
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConstants.COMMENT_EXCHANGE,
                    RabbitMQConstants.COMMENT_DEC_ROUTING_KEY,
                    map);
        } catch (AmqpException e) {
            log.error(e.toString());
        }
    }
}




