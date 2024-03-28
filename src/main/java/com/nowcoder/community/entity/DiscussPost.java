package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

@Data
public class DiscussPost {
    /**
     * 帖子id
     */
    private Integer id;
    /**
     * 发帖用户id
     */
    private Integer userId;
    /**
     * 帖子标题
     */
    private String title;
    /**
     * 帖子内容
     */
    private String content;
    /**
     * 帖子类型
     * 0-普通
     * 1-置顶
     */
    private Integer type;
    /**
     * 帖子状态
     * 0-正常
     * 1-精华
     * 2-拉黑
     */
    private Integer status;
    private Date createTime;
    private Integer commentCount;
    private Double score;
}
