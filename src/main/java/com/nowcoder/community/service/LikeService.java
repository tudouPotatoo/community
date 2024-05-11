package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.HostHolder;
import com.nowcoder.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
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
     *             将目标用户的点赞数量-1
     *    2.2 不是-->点赞
     *             将当前用户加入set
     *             将目标用户的点赞数量+1
     * 3. 将最新的点赞数量以及点赞状态加入map
     * 4. 返回map
     * @param entityType 实体类型
     * @param entityId 实体id
     * @param entityType
     * @param entityId
     * @return
     */
    public Map<String, Object> like(int entityType, int entityId, int entityAuthorId) {
        // User user = hostHolder.getUser();
        // String idStr = String.valueOf(user.getId());
        // Map<String, Object> res = new HashMap<>();
        // // 1. 根据实体类型和id拼接key like:entityType:entityId
        // String key = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // // 2. 查看当前用户是否已经点过赞
        // int likeStatus = getLikeStatus(entityType, entityId);
        // // 2.1 是-->取消点赞
        // if (likeStatus == LIKE_STATUS) {
        //     // 将该用户从set中移除
        //     redisTemplate.opsForSet().remove(key, idStr);
        //     // 3. 将点赞状态加入map
        //     res.put("likeStatus", UNLIKE_STATUS);
        // } else {
        //     // 2.2 不是-->点赞
        //     // 将当前用户加入set
        //     redisTemplate.opsForSet().add(key, idStr);
        //     // 3. 将点赞状态加入map
        //     res.put("likeStatus", LIKE_STATUS);
        // }
        //
        // // 3. 将点赞数量加入map
        // res.put("likeCount", redisTemplate.opsForSet().size(key));
        // return  res;

        /**
         * 点赞
         * 1. 根据实体类型和id拼接key
         * 2. 根据实体作者id拼接key
         * 3. 查看当前用户是否已经点过赞
         *    3.0 开启事务
         *    3.1 是-->取消点赞
         *             将该用户从set中移除
         *             将目标用户的点赞数量-1
         *    3.2 不是-->点赞
         *             将当前用户加入set
         *             将目标用户的点赞数量+1
         *    3.3 提交事务
         * 4. 将最新的点赞数量以及点赞状态加入map
         * 5. 返回map
         */
        Map<String, Object> res = new HashMap<>();

        // 1. 根据实体类型和id拼接key
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // 2. 根据实体作者id拼接key
        String authorLikeKey = RedisKeyUtil.getUserLikeKey(entityAuthorId);
        // 3. 查看当前用户是否已经点过赞
        User user = hostHolder.getUser();
        String idStr = String.valueOf(user.getId());
        boolean liked = redisTemplate.opsForSet().isMember(entityLikeKey, idStr).booleanValue();
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                // 3.0 开启事务
                redisOperations.multi();
                // 3.1 是-->取消点赞
                if (liked) {
                    // 将该用户从set中移除
                    redisTemplate.opsForSet().remove(entityLikeKey, idStr);
                    // 将目标用户的点赞数量-1
                    redisTemplate.opsForValue().decrement(authorLikeKey);

                    // 3.2 不是-->点赞
                } else {
                    // 将当前用户加入set
                    redisTemplate.opsForSet().add(entityLikeKey, idStr);
                    // 将目标用户的点赞数量+1
                    redisTemplate.opsForValue().increment(authorLikeKey);
                }

                // 3.3 提交事务
                return redisOperations.exec();
            }
        });

        // 4. 将最新的点赞数量以及点赞状态加入map
        res.put("likeStatus", liked ? UNLIKE_STATUS : LIKE_STATUS);
        res.put("likeCount", redisTemplate.opsForSet().size(entityLikeKey));
        // 5. 返回map
        return res;
    }

    /**
     * 获取帖子/评论点赞数量
     * 1. 获取key
     * 2. 根据key获取redis中对应的val set的size
     * @param entityType
     * @param entityId
     * @return
     */
    public long getEntityLikeCount(int entityType, int entityId) {
        String key = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        long likeCount = redisTemplate.opsForSet().size(key);
        return likeCount;
    }

    /**
     * 获取用户获得到点赞数量
     * @param userId
     * @return
     */
    public long getUserLikeCount(int userId) {
        String key = RedisKeyUtil.getUserLikeKey(userId);
        String count = redisTemplate.opsForValue().get(key);
        return count == null ? 0 : Integer.valueOf(count);
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
        String key = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // 检查当前用户是否点赞
        boolean isMember = redisTemplate.opsForSet().isMember(key, idStr).booleanValue();
        return isMember ? LIKE_STATUS : UNLIKE_STATUS;
    }
}
