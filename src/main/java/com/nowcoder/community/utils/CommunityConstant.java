package com.nowcoder.community.utils;

public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;
    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;
    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 登陆时不勾选【记住我】登陆凭证默认有效时长：12h
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 登陆时勾选【记住我】登陆凭证有效时长：100天
     */
    int REMEMBER_ME_EXPIRED_SECONDS = 3600 * 24 * 100;

    /**
     * 实体类型-帖子
     */
    int ENTITY_TYPE_POST = 1;
    /**
     * 实体类型-评论
     */
    int ENTITY_TYPE_COMMENT = 2;
    /**
     * 实体类型-用户
     */
    int ENTITY_TYPE_USER = 3;
    /**
     * 未读消息状态
     */
    int UNREAD_MESSAGE_STATUS = 0;

    /**
     * 已读消息状态
     */
    int READ_MESSAGE_STATUS = 1;

    /**
     * 已删除消息状态
     */
    int DELETED_MESSAGE_STATUS = 2;

    /**
     * 异步请求返回状态码
     * 成功
     */
    int SERVER_SUCCESS = 1;

    /**
     * 异步请求返回状态码
     * 失败
     */
    int SERVER_FAIL = -1;

    /**
     * 用户点赞状态
     * 已点赞
     */
    int LIKE_STATUS = 1;
    /**
     * 用户点赞状态
     * 未点赞
     */
    int UNLIKE_STATUS = 0;
}
