package com.enndfp.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 消息查询请求体
 *
 * @author Enndfp
 */
@Data
public class MessageQueryRequest implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 页数
     */
    private Integer current;

    /**
     * 每页多少条
     */
    private Integer pageSize;

    private static final long serialVersionUID = 7864681499849148540L;
}
