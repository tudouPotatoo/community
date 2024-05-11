package com.nowcoder.community.utils;

public class RedisKeyUtil implements CommunityConstant{

    private static final String PREFIX = "like";

    private static final String SPLIT = ":";

    /**
     * 根据entityType和entityId生成key
     * value存储帖子/评论获得的点赞
     * @param entityType
     * @param entityId
     * @return
     */
    // like:post:1 --> {userId1, userId2, userId3...}
    public static String getEntityLikeKey(int entityType, int entityId) {
        String key = PREFIX + SPLIT + (entityType == ENTITY_TYPE_POST ? "post" : "comment") + SPLIT + entityId;
        return key;
    }

    /**
     * 根据userId生成key
     * value对存储用户获得的点赞数量
     * @param userId
     * @return
     */
    // like:user:1 --> 10
    public static String getUserLikeKey(int userId) {
        String key = PREFIX + SPLIT + "user" + SPLIT + userId;
        return key;
    }
}
