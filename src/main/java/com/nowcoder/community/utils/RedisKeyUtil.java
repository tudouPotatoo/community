package com.nowcoder.community.utils;

public class RedisKeyUtil implements CommunityConstant{

    private static final String PREFIX = "like";

    private static final String SPLIT = ":";

    /**
     * 粉丝
     */
    private static final String FOLLOWER = "follower";
    /**
     * 关注的人
     */
    private static final String FOLLOWEE = "followee";

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

    /**
     * 获取用户所关注的所有某种类型的实体（例如：关注的人）
     * @param userId 用户id
     * @param entityType 实体类型
     * @return
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 获取实体的所有粉丝
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }
}
