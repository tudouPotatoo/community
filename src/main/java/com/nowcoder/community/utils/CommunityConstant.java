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

}
