package com.nowcoder.community.service;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.mapper.CommentMapper;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.HostHolder;
import com.nowcoder.community.utils.SensitiveWordsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

@Service
public class CommentService implements CommunityConstant {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private SensitiveWordsFilter sensitiveWordsFilter;

    public List<Comment> getCommentByEntityId(int entityType, int entityId, int offset, int pageSize) {
        return commentMapper.selectCommentByEntityId(entityType, entityId, offset, pageSize);
    }

    public int getCountByEntityId(int entityType, int entityId) {
        return commentMapper.selectCountByEntityId(entityType, entityId);
    }

    /**
     * 新增评论
     * 1. 完善评论数据
     * 2. 过滤评论内容
     * 3. 往数据库新增一条评论
     * 4. 更新帖子的评论数量
     * @param comment
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        // 1. 完善评论数据
        comment.setStatus(1);
        comment.setCreateTime(new Date());
        comment.setUserId(hostHolder.getUser().getId());
        if (comment.getTargetId() == null) {
            comment.setTargetId(0);
        }
        // 2. 过滤评论内容
        String content = comment.getContent();
        content = HtmlUtils.htmlEscape(content);
        content = sensitiveWordsFilter.filter(content);
        comment.setContent(content);
        // 3. 往数据库新增一条评论
        int rows = commentMapper.insertComment(comment);
        // 如果评论的对象是帖子
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            // 4. 更新帖子的评论数量
            Integer discussPostId = comment.getEntityId();
            DiscussPost discussPost = discussPostService.getDiscussPostById(discussPostId);
            Integer commentCount = discussPost.getCommentCount();
            discussPostService.updateDiscussPostCommentCount(discussPostId, commentCount + rows);
        }
        return rows;
    }
}
