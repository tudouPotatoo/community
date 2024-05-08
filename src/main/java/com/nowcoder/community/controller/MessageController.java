package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会话-一个对话框是一个会话
 * 私信-一条消息是一个私信
 */
@Controller
@RequestMapping("/message")
public class MessageController implements CommunityConstant {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    /**
     * 会话列表
     * 0. 获取当前用户
     * 1. 设置分页信息
     * 2. 根据当前用户获取会话列表
     * 3. 完善显示信息
     *    3.1 总的未读私信数量
     *    3.2 每一个对话包含的内容
     *      3.2.1 会话本身
     *      3.2.2 对话用户
     *      3.2.3 总的私信数量
     *      3.2.4 该对话未读私信数量
     * 4. 返回封装好的内容 跳转到letter页面
     * @param mv
     * @param page
     * @return
     */
    @GetMapping("/list")
    public ModelAndView getConversationList(ModelAndView mv, Page page) {
        // 0. 获取当前用户
        User user = hostHolder.getUser();
        int conversationCount = messageService.getConversationCount(user.getId());
        // 1. 设置分页信息
        page.setPath("/message/list");
        page.setPageSize(5);
        page.setRows(conversationCount);
        // 2. 根据当前用户获取会话列表
        List<Message> conversationList = messageService.getConversationList(user.getId(), page.getOffset(), page.getPageSize());
        // 3. 完善显示信息
        List<Map<String, Object>>  conversationVoList = new ArrayList<>();
        // 3.1 总的未读私信数量
        int totalUnreadMsgCount = messageService.getUnreadMessageCount(user.getId(), null);
        mv.addObject("totalUnreadMsgCount", totalUnreadMsgCount);
        // 3.2 每一个对话包含的内容
        for (Message conversation : conversationList) {
            Map<String, Object> conversationVo = new HashMap<>();
            // 3.2.1 会话本身
            conversationVo.put("conversation", conversation);
            // 3.2.2 对话用户
            int targetUserId = user.getId() == conversation.getFromId() ? conversation.getToId() : conversation.getFromId();
            conversationVo.put("targetUser", userService.getUserById(targetUserId));
            // 3.2.3 总的私信数量
            int dmCount = messageService.getDirectMessageCount(conversation.getConversationId());
            conversationVo.put("dmCount", dmCount);
            // 3.2.4 该对话未读私信数量
            int unreadMsgCount = messageService.getUnreadMessageCount(user.getId(), conversation.getConversationId());
            conversationVo.put("unreadMsgCount", unreadMsgCount);
            conversationVoList.add(conversationVo);
        }
        // 4. 返回封装好的内容 跳转到letter页面
        mv.addObject("conversationVoList", conversationVoList);
        mv.setViewName("/site/letter");
        return mv;
    }

    /**
     * 获取一个会话的详情
     * 0. 设置分页信息
     * 1. 获取会话的目标用户
     * 2. 根据会话id分页查询私信列表
     * 3. 完善显示信息
     *    3.1 私信本身
     *    3.2 私信的发送者
     *  4. 返回封装好的内容 准备跳转到letter-detail页面
     *  5. 将未读消息状态设为已读
     *  6. 返回结果
     * @param conversationId
     * @param mv
     * @param page
     * @return
     */
    @GetMapping("/detail/{conversationId}")
    public ModelAndView getConversationDetail(@PathVariable String conversationId, ModelAndView mv, Page page) {
        // 0. 设置分页信息
        page.setPath("/message/detail/" + conversationId);
        page.setPageSize(10);
        page.setRows(messageService.getDirectMessageCount(conversationId));
        // 1. 获取会话的目标用户
        User targerUser = getTargerUser(conversationId);
        mv.addObject("targetUser", targerUser);
        // 2. 根据会话id分页查询私信列表
        List<Message> directMessageList = messageService.getDirectMessageList(conversationId, page.getOffset(), page.getPageSize());
        // 3. 完善显示信息
        List<Map<String, Object>> directMessageVoList = new ArrayList<>();
        for (Message message : directMessageList) {
            Map<String, Object> directMessageVo = new HashMap<>();
            // 3.1 私信本身
            directMessageVo.put("message", message);
            // 3.2 私信的发送者
            User sender = userService.getUserById(message.getFromId());
            directMessageVo.put("sender", sender);

            directMessageVoList.add(directMessageVo);
        }
        // 4. 返回封装好的内容 准备跳转到letter-detail页面
        mv.addObject("directMessageVoList", directMessageVoList);
        mv.setViewName("/site/letter-detail");

        // 5. 将未读消息状态设为已读
        List<Message> unreadMessageList = new ArrayList<>();
        for (Message message : directMessageList) {
            if (message.getStatus() == UNREAD_MESSAGE_STATUS) {
                unreadMessageList.add(message);
            }
        }
        messageService.readMessage(unreadMessageList);
        // 6. 返回结果
        return mv;
    }

    /**
     * 根据conversationId获取会话的目标用户
     * @param conversationId
     * @return
     */
    private User getTargerUser(String conversationId) {
        String[] userIds = conversationId.split("_");
        int userId1 = Integer.parseInt(userIds[0]);
        int userId2 = Integer.parseInt(userIds[1]);

        int hostUserId = hostHolder.getUser().getId();
        if (userId1 == hostUserId) {
            return userService.getUserById(userId2);
        } else {
            return userService.getUserById(userId1);
        }
    }

    /**
     * 当前用户给其他用户发送一条私信
     * @param targetUsername 目标用户的用户名
     * @param content 私信内容
     * @return
     */
    @PostMapping("/send")
    @ResponseBody
    private String sendMessage(String targetUsername, String content) {
        return messageService.addMessage(targetUsername, content);
    }
}
