package com.nowcoder.community.service;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.mapper.DiscussPostMapper;
import com.nowcoder.community.utils.HostHolder;
import com.nowcoder.community.utils.SensitiveWordsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private SensitiveWordsFilter sensitiveWordsFilter;

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

    /**
     * 增加一条帖子
     * 1. 将title、content中的html标识符进行转义
     * 2. 对title、content进行敏感词过滤
     * 3. 创建帖子对象
     * 4. 将该对象存入数据库
     */
    public void addDiscussPost(String title, String content) {
        // 1. 将title、content中的html标识符进行转义
        // 将类似于<h1>这样的html可以解析的文本转化为纯文本，避免这些内容被解析为页面的一部分，防止xss等注入攻击
        title = HtmlUtils.htmlEscape(title);
        content = HtmlUtils.htmlEscape(content);

        // 2. 对title、content进行敏感词过滤
        title = sensitiveWordsFilter.filter(title);
        content = sensitiveWordsFilter.filter(content);
        
        // 3. 创建帖子对象
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(hostHolder.getUser().getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setType(0);
        discussPost.setStatus(0);
        discussPost.setCreateTime(new Date());
        discussPost.setCommentCount(0);
        discussPost.setScore(0.0);

        // 4. 将该对象存入数据库
        discussPostMapper.insertDiscussPost(discussPost);
    }

    /**
     * 根据id获取帖子信息
     * @param id
     * @return
     */
    public DiscussPost getDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }
}
