package com.nowcoder.community.mapper;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    /**
     * 分页查询用户的会话列表
     * @param userId
     * @param offset
     * @param pageSize
     * @return
     */
    public List<Message> selectConversationList(int userId, int offset, int pageSize);

    /**
     * 查询用户的总会话数量
     * @param userId
     * @return
     */
    public int selectConversationCount(int userId);

    /**
     * 分页查询一个会话的私信列表
     * @param conversationId
     * @param offset
     * @param pageSize
     * @return
     */
    public List<Message> selectDirectMessageList(String conversationId, int offset, int pageSize);

    /**
     * 查询一个会话的私信总量
     * @param conversationId
     * @return
     */
    public int selectDirectMessageCount(String conversationId);

    /**
     * 查询未读消息数量
     * 当conversationId不为空时-->某个特定会话的未读消息数量
     * 当conversationId为空时-->所有会话的未读消息数量
     * @param userId
     * @param conversationId
     * @return
     */
    public int selectUnreadMessageCount(int userId, String conversationId);

    /**
     * 插入一条新的消息
     * @param message
     * @return
     */
    public int insertMessage(Message message);

    /**
     * 更新一组Message对象的状态
     * @param messageList
     * @param status
     * @return
     */
    public int updateStatus(List<Message> messageList, int status);

    /**
     * 根据id查询消息
     * @param id
     * @return
     */
    public Message selectById(int id);
}
