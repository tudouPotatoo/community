package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    /**
     * 用户id
     */
    private Integer id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 盐
     */
    private String salt;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 用户类型
     * 0-普通用户
     * 1-超级管理员
     * 2-版主
     */
    private Integer type;
    /**
     * 0-未激活
     * 1-已激活
     */
    private Integer status;
    /**
     * 激活码
     */
    private String activationCode;
    /**
     * 头像地址
     */
    private String headerUrl;
    /**
     * 用户创建时间
     */
    private Date createTime;
}
