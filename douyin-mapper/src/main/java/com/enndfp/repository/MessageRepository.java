package com.enndfp.repository;

import com.enndfp.mo.MessageMO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Enndfp
 */
@Repository
public interface MessageRepository extends MongoRepository<MessageMO, Long> {

    /**
     * 根据接收用户id按发送时间降序分页查询
     *
     * @param toUserId
     * @param pageable
     * @return
     */
    List<MessageMO> findAllByToUserIdEqualsOrderByCreateTimeDesc(Long toUserId, Pageable pageable);

    // TODO 删除消息
    //void deleteAllByFromUserIdAndToUserIdAndMsgType();
}
