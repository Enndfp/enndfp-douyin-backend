package com.enndfp.service;

import com.enndfp.dto.message.MessageQueryRequest;
import com.enndfp.mo.MessageMO;

import java.util.List;
import java.util.Map;

/**
 * @author Enndfp
 */
public interface MessageService {

    /**
     * 创建消息
     *
     * @param fromUserId
     * @param toUserId
     * @param msgType
     * @param msgContent
     */
    void createMsg(Long fromUserId, Long toUserId, Integer msgType, Map<String, Object> msgContent);

    /**
     * 分页查询消息
     *
     * @param messageQueryRequest@return
     */
    List<MessageMO> queryList(MessageQueryRequest messageQueryRequest);
}
