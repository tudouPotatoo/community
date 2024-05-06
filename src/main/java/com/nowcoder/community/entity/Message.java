package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * 私信
 */
@Data
public class Message {
    private Integer id;
    /**
     * 消息发送者
     * 当fromId=1说明是系统消息
     */
    private Integer fromId;
    /**
     * 消息接收者
     */
    private Integer toId;
    /**
     * 会话id
     * 111_112 --> _两边分别是消息请求/接受方的id，id小的放前面
     */
    private String conversationId;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 消息状态
     * 0-未读
     * 1-已读
     * 2-已删除
     */
    private Integer status;
    /**
     * 消息发送时间
     */
    private Date createTime;
}
