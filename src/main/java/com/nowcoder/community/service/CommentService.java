package com.nowcoder.community.service;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    public List<Comment> getCommentByEntityId(int entityType, int entityId, int offset, int pageSize) {
        return commentMapper.selectCommentByEntityId(entityType, entityId, offset, pageSize);
    }

    public int getCountByEntityId(int entityType, int entityId) {
        return commentMapper.selectCountByEntityId(entityType, entityId);
    }
}
