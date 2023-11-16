package com.enndfp.service.impl;

import com.enndfp.common.ErrorCode;
import com.enndfp.enums.MessageEnum;
import com.enndfp.message.MessageQueryRequest;
import com.enndfp.mo.MessageMO;
import com.enndfp.pojo.User;
import com.enndfp.repository.MessageRepository;
import com.enndfp.service.MessageService;
import com.enndfp.service.UserService;
import com.enndfp.utils.RedisUtils;
import com.enndfp.utils.ThrowUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.enndfp.constant.PageConstants.DEFAULT_CURRENT_ZERO;
import static com.enndfp.constant.PageConstants.DEFAULT_PAGE_SIZE;
import static com.enndfp.constant.RedisConstants.FANS_AND_VLOGER_RELATIONSHIP_KEY;

/**
 * @author Enndfp
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageRepository messageRepository;
    @Resource
    private UserService userService;
    @Resource
    private RedisUtils redisUtils;

    @Override
    public void createMsg(Long fromUserId, Long toUserId, Integer msgType, Map<String, Object> msgContent) {
        // 1. 校验请求参数
        if (fromUserId == null || toUserId == null || msgType == null) {
            ThrowUtils.throwException(ErrorCode.PARAMS_ERROR);
        }

        // 2. 根据id获得发送消息的用户信息
        User fromUser = userService.getById(fromUserId);

        // 3. 创建消息对象
        MessageMO messageMO = new MessageMO();
        messageMO.setFromUserId(fromUserId);
        messageMO.setFromNickname(fromUser.getNickname());
        messageMO.setFromFace(fromUser.getFace());
        messageMO.setToUserId(toUserId);
        messageMO.setMsgType(msgType);
        if (msgContent != null) messageMO.setMsgContent(msgContent);
        messageMO.setCreateTime(new Date());

        // 4. 保存入库
        messageRepository.save(messageMO);
    }

    @Override
    public List<MessageMO> queryList(MessageQueryRequest messageQueryRequest) {
        Long userId = messageQueryRequest.getUserId();
        Integer current = messageQueryRequest.getCurrent();
        Integer pageSize = messageQueryRequest.getPageSize();

        // 1. 校验请求参数
        ThrowUtils.throwIf(userId == null, ErrorCode.PARAMS_ERROR);
        // MongoDB 从0分页，区别于数据库
        if (current == null) current = DEFAULT_CURRENT_ZERO;
        if (pageSize == null) pageSize = DEFAULT_PAGE_SIZE;

        // 2. 构造分页器
        Pageable pageable = PageRequest.of(current, pageSize, Sort.Direction.DESC, "createTime");

        // 3. 查询数据库
        List<MessageMO> messageMOList = messageRepository.
                findAllByToUserIdEqualsOrderByCreateTimeDesc(userId, pageable);

        // 4. 处理后置逻辑
        for (MessageMO messageMO : messageMOList) {
            Integer msgType = messageMO.getMsgType();

            // 4.1 如果是关注消息，判断双方是否互关
            if (MessageEnum.FOLLOW_YOU.type.equals(msgType)) {
                Map<String, Object> msgContent = messageMO.getMsgContent();
                if (msgContent == null) msgContent = new HashMap<>();

                // 4.2 从 Redis 中取出双方的关系
                String relationship = redisUtils.get(FANS_AND_VLOGER_RELATIONSHIP_KEY
                        + messageMO.getToUserId() + ":" + messageMO.getFromUserId());

                // 4.3 不为空，则是互关
                if (StringUtils.isNotBlank(relationship)) {
                    msgContent.put("isFriend", true);
                } else {
                    msgContent.put("isFriend", false);
                }
                messageMO.setMsgContent(msgContent);
            }
        }
        return messageMOList;
    }
}
