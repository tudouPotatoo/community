package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LikeService implements CommunityConstant {
    private static final String SPLIT = ":";
    private static final String PREFIX = "like";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 点赞
     * 1. 根据实体类型和id拼接key
     * 2. 查看当前用户是否已经点过赞
     *    2.1 是-->取消点赞
     *             将该用户从set中移除
     *    2.2 不是-->点赞
     *             将当前用户加入set
     * 3. 将最新的点赞数量以及点赞状态加入map
     * 4. 返回map
     * @param entityType 实体类型
     * @param entityId 实体id
     * @param entityType
     * @param entityId
     * @return
     */
    public Map<String, Object> like(int entityType, int entityId) {
        User user = hostHolder.getUser();
        String idStr = String.valueOf(user.getId());
        Map<String, Object> res = new HashMap<>();
        // 1. 根据实体类型和id拼接key like:entityType:entityId
        String key = generateKey(entityType, entityId);
        // 2. 查看当前用户是否已经点过赞
        int likeStatus = getLikeStatus(entityType, entityId);
        // 2.1 是-->取消点赞
        if (likeStatus == LIKE_STATUS) {
            // 将该用户从set中移除
            redisTemplate.opsForSet().remove(key, idStr);
            // 3. 将点赞状态加入map
            res.put("likeStatus", UNLIKE_STATUS);
        } else {
            // 2.2 不是-->点赞
            // 将当前用户加入set
            redisTemplate.opsForSet().add(key, idStr);
            // 3. 将点赞状态加入map
            res.put("likeStatus", LIKE_STATUS);
        }

        // 3. 将点赞数量加入map
        res.put("likeCount", redisTemplate.opsForSet().size(key));
        return  res;
    }

    /**
     * 获取点赞数量
     * 1. 获取key
     * 2. 根据key获取redis中对应的val set的size
     * @param entityType
     * @param entityId
     * @return
     */
    public long getLikeCount(int entityType, int entityId) {
        String key = generateKey(entityType, entityId);
        long likeCount = redisTemplate.opsForSet().size(key);
        return likeCount;
    }

    /**
     * 获取点赞状态
     * 1. 获取当前用户id
     * 2. 获取key
     * 3. 检查key对应的val set是否包含用户id
     *      3.1 是 说明用户已经点赞
     *      3.2 不是 说明用户未点赞
     * @param entityType
     * @param entityId
     * @return
     */
    public int getLikeStatus(int entityType, int entityId) {
        // 获取当前用户id
        User user = hostHolder.getUser();
        String idStr = String.valueOf(user.getId());
        // 获取键值
        String key = generateKey(entityType, entityId);
        // 检查当前用户是否点赞
        boolean isMember = redisTemplate.opsForSet().isMember(key, idStr).booleanValue();
        return isMember ? LIKE_STATUS : UNLIKE_STATUS;
    }

    /**
     * 根据entityType和entityId生成key
     * @param entityType
     * @param entityId
     * @return
     */
    private String generateKey(int entityType, int entityId) {
        String key = PREFIX + SPLIT + (entityType == ENTITY_TYPE_POST ? "post" : "comment") + SPLIT + entityId;
        return key;
    }
}
