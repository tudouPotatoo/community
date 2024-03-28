package com.nowcoder.community.service;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.mapper.DiscussPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    /**
     * 当userId = 0时，用于获取该分页下所有帖子
     * 当userId > 0时，用于获取对应用户该分页下所有帖子
     * @param userId 用户id
     * @param offset 分页的起始行
     * @param pageSize 分页的大小
     * @return 查询到的帖子
     */
    public List<DiscussPost> getDiscussPosts(int userId, int offset, int pageSize) {
        return discussPostMapper.selectDiscussPosts(userId, offset, pageSize);
    }

    /**
     * 获取帖子的总数量 便于进行分页
     * 当userId = 0时，用于获取所有帖子的总数量
     * 当userId > 0时，用于获取对应用户的所有帖子的总数量
     * @param userId 用户Id
     * @return 帖子总个数
     */
    public int getDiscussPostCount(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }
}
