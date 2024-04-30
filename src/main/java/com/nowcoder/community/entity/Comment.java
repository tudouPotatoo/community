package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Comment {
    /**
     * 评论id
     */
    private Integer id;

    /**
     * 用户Id
     */
    private Integer userId;

    /**
     * 评论的对象类型
     * 1-帖子
     * 2-评论
     */
    private Integer entityType;

    /**
     * 评论对象的id
     * 例如：
     * 这是一条对帖子的评论 entityId即为帖子Id
     * 这是一条对评论的回复 entityId即为评论Id
     */
    private Integer entityId;

    /**
     * 回复的用户id
     * 当这是一条帖子的评论，targetId为null即可
     * 只有当这是一条针对某用户的评论的回复时，targetId为对应用户id
     */
    private Integer targetId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论状态
     * 0-已删除
     * 1-正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;
}
