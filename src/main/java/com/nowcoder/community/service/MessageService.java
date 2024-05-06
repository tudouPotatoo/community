package com.nowcoder.community.service;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    /**
     * 分页查询用户的会话列表
     * @param userId
     * @param offset
     * @param pageSize
     * @return
     */
    public List<Message> getConversationList(int userId, int offset, int pageSize) {
        return messageMapper.selectConversationList(userId, offset, pageSize);
    }

    /**
     * 查询用户的总会话数量
     * @param userId
     * @return
     */
    public int getConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    /**
     * 分页查询一个会话的私信列表
     * @param conversationId
     * @param offset
     * @param pageSize
     * @return
     */
    public List<Message> getDirectMessageList(String conversationId, int offset, int pageSize) {
        return messageMapper.selectDirectMessageList(conversationId, offset, pageSize);
    }

    /**
     * 查询一个会话的私信总量
     * @param conversationId
     * @return
     */
    public int getDirectMessageCount(String conversationId) {
        return messageMapper.selectDirectMessageCount(conversationId);
    }

    /**
     * 查询未读消息数量
     * 当conversationId不为空时-->某个特定会话的未读消息数量
     * 当conversationId为空时-->所有会话的未读消息数量
     * @param userId
     * @param conversationId
     * @return
     */
    public int getUnreadMessageCount(int userId, String conversationId) {
        return messageMapper.selectUnreadMessageCount(userId, conversationId);
    }
}
