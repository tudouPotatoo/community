package com.nowcoder.community.utils;

import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
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

    /**
     * 将返回结果转化为json字符串
     * @param code
     * @param msg
     * @param map
     * @return
     */
    public static String getJsonString(int code, String msg, Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        }

        return jsonObject.toJSONString();
    }

    public static String getJsonString(int code, String msg) {
        return getJsonString(code, msg, null);
    }

    public static String getJsonString(int code) {
        return  getJsonString(code, null, null);
    }

}
