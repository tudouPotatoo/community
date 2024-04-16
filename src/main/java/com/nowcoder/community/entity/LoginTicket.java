package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoginTicket {
    private Integer id;
    private Integer userId;
    private String ticket;
    /**
     * 当前登陆凭证是否有效
     * 0-无效
     * 1-有效
     */
    private int status;
    /**
     * 该登陆凭证过期的时间
     */
    private Date expired;
}
