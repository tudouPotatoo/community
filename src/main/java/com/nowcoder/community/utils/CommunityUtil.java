package com.nowcoder.community.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {
    /**
     * 生成随机字符串
     * @return uuid字符串
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 对密码进行 加盐、加密
     * @return
     */
    public static String md5(String password) {
        if (StringUtils.isBlank(password)) {
            return null;
        } else {
            return DigestUtils.md5DigestAsHex(password.getBytes());
        }
    }
}
