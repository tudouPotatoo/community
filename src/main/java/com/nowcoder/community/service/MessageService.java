package com.nowcoder.community.service;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.mapper.MessageMapper;
import com.nowcoder.community.mapper.UserMapper;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.HostHolder;
import com.nowcoder.community.utils.SensitiveWordsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

@Service
public class MessageService implements CommunityConstant {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SensitiveWordsFilter sensitiveWordsFilter;

    @Autowired
    private HostHolder hostHolder;

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

    /**
     * 添加一条新消息
     * 1. 根据用户名查询用户
     *      用户不存在 --> 返回错误信息
     *      用户存在 --> 继续向下
     * 2. 过滤消息内容
     * 3. 创建Message对象，完善信息
     * 4. 调用Mapper层方法，插入Message到数据库
     * @param targetUsername 消息的接收方
     * @param content 消息内容
     * @return 发送消息结果
     */
    public String addMessage(String targetUsername, String content) {
        // 1. 根据用户名查询用户
        User targetUser = userMapper.selectByUsername(targetUsername);
        // 用户不存在 --> 返回错误信息
        if (targetUser == null) {
            return CommunityUtil.getJsonString(-1, "该用户不存在！");
        }
        // 用户存在 --> 继续向下

        // 2. 过滤消息内容
        content = HtmlUtils.htmlEscape(content);
        content = sensitiveWordsFilter.filter(content);

        // 3. 创建Message对象，完善信息
        Message message = new Message();
        int fromId = hostHolder.getUser().getId();
        message.setFromId(fromId);
        int toId = targetUser.getId();
        message.setToId(toId);
        String conversationId = null;
        if (fromId < toId) {
            conversationId = fromId + "_" + toId;
        } else {
            conversationId = toId + "_" + fromId;
        }
        message.setConversationId(conversationId);
        message.setContent(content);
        message.setCreateTime(new Date());
        message.setStatus(0);  // 状态设为未读

        // 4. 调用Mapper层方法，插入Message到数据库
        messageMapper.insertMessage(message);

        return CommunityUtil.getJsonString(0, "消息已成功发送！");
    }

    /**
     * 将下列消息的状态设为已读
     * @param messageList
     * @return
     */
    public int readMessage(List<Message> messageList) {
        return messageMapper.updateStatus(messageList, READ_MESSAGE_STATUS);
    }
}
