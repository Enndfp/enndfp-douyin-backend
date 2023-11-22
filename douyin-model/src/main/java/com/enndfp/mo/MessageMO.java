package com.enndfp.mo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;

/**
 * @author Enndfp
 */
@Data
@Document("message")
public class MessageMO {

    /**
     * 消息主键id
     */
    @Id
    private String id;

    /**
     * 消息来自的用户id
     */
    @Field("fromUserId")
    private Long fromUserId;

    /**
     * 消息来自的用户昵称
     */
    @Field("fromNickname")
    private String fromNickname;

    /**
     * 消息来自的用户头像
     */
    @Field("fromFace")
    private String fromFace;

    /**
     * 消息接收的用户id
     */
    @Field("toUserId")
    private Long toUserId;

    /**
     * 消息类型 枚举
     */
    @Field("msgType")
    private Integer msgType;

    /**
     * 消息内容
     */
    @Field("msgContent")
    private Map<String,Object> msgContent;

    /**
     * 消息创建时间
     */
    @Field("createTime")
    private Date createTime;
}
