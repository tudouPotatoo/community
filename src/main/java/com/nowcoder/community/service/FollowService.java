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

@Service
public class FollowService {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 关注
     * 1. 获取当前用户
     * 2. 获取实体粉丝key
     * 3. 获取用户关注的该类实体key
     * 4. 实体粉丝zset中添加当前用户id
     * 5. 用户关注的该类实体zset中添加该实体id
     * （注意 由于涉及两次redis的写操作，需要使用事务）
     * @param entityType 要关注的实体类型(用户、帖子、课程等)
     * @param entityId 要关注的实体id
     */
    public void follow(int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                // 开启事务
                operations.multi();

                // 1. 获取当前用户
                User user = hostHolder.getUser();
                Integer userId = user.getId();
                // 2. 获取实体粉丝key
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                // 3. 获取用户关注的该类实体key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                // 4. 实体粉丝zset中添加当前用户id
                redisTemplate.opsForZSet().add(followerKey, userId.toString(), System.currentTimeMillis());
                // 5. 用户关注的该类实体zset中添加该实体id
                redisTemplate.opsForZSet().add(followeeKey, String.valueOf(entityId), System.currentTimeMillis());

                // 提交事务
                return operations.exec();
            }
        });
    }

    /**
     * 取消关注
     * 1. 获取当前用户
     * 2. 获取实体粉丝key
     * 3. 获取用户关注的该类实体key
     * 4. 实体粉丝zset中删除当前用户id
     * 5. 用户关注的该类实体zset中删除该实体id
     * （注意 由于涉及两次redis的写操作，需要使用事务）
     * @param entityType 要关注的实体类型(用户、帖子、课程等)
     * @param entityId 要关注的实体id
     */
    public void unfollow(int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                // 开启事务
                operations.multi();

                // 1. 获取当前用户
                User user = hostHolder.getUser();
                Integer userId = user.getId();
                // 2. 获取实体粉丝key
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                // 3. 获取用户关注的该类实体key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                // 4. 实体粉丝zset中删除当前用户id
                redisTemplate.opsForZSet().remove(followerKey, userId.toString());
                // 5. 用户关注的该类实体zset中删除该实体id
                redisTemplate.opsForZSet().remove(followeeKey, String.valueOf(entityId));

                // 提交事务
                return operations.exec();
            }
        });
    }

    /**
     * 获取某用户关注的某种实体的数量
     * （例如：用户关注了多少个人）
     * 1. 获取用户关注的该类实体key
     * 2. 获取用户关注的该类实体zset的大小 即为关注的个数
     * 3. 返回结果
     * @param userId 用户id
     * @param entityType 实体类型
     * @return 关注数
     */
    public long getFolloweeCount(int userId, int entityType) {
        // 1. 获取用户关注的该类实体key
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        // 2. 获取用户关注的该类实体zset的大小 即为关注的个数
        Long followeeCount = redisTemplate.opsForZSet().size(followeeKey);
        // 3. 返回结果
        return followeeCount.longValue();
    }

    /**
     * 获取当前实体的粉丝数量
     * （例如：当前用户的粉丝数量）
     * 1. 获取实体粉丝key
     * 2. 获取实体粉丝zset的大小 即为粉丝数量
     * 3. 返回结果
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return 粉丝数
     */
    public long getFollowerCount(int entityType, int entityId) {
        // 1. 获取实体粉丝key
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        // 2. 获取实体粉丝zset的大小 即为粉丝数量
        Long followerCount = redisTemplate.opsForZSet().size(followerKey);
        // 3. 返回结果
        return followerCount.longValue();
    }

    /**
     * 判断当前用户是否已经关注targetUser
     * 0. 获取当前用户
     * 1. 获取当前用户关注的USER key
     * 2. 判断用户关注的USER的zset是否包含targetUserId
     *      2.1 包含 当前用户是targetUser的粉丝
     *      2.2 不包含 当前用户未关注targetUser
     * @return
     */
    /**
     * 判断某用户是否关注的某个实体
     * 1. 获取用户关注的该类实体 key
     * 2. 判断用户关注的该类实体zset是否包含entityId
     *      2.1 包含 已关注
     *      2.2 不包含 未关注
     * @param userId 用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return
     */
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        // 1. 获取用户关注的该类实体 key
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        // 2. 判断用户关注的该类实体zset是否包含entityId
        Long rank = redisTemplate.opsForZSet().rank(followeeKey, String.valueOf(entityId));
        // rank!=null 说明已关注；rank==null 说明未关注；
        return rank != null;
    }
}
