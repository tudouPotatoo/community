package com.nowcoder.community.mapper;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    /**
     * 根据entityId查询所有评论
     * @param entityType 评论对象类型
     * @param entityId 评论id
     * @return
     */
    public List<Comment> selectCommentByEntityId(int entityType, int entityId, int offset, int pageSize);

    /**
     * 根据entityId查询所有评论的数量
     * @param entityType 评论对象类型
     * @param entityId 评论id
     * @return
     */
    public int selectCountByEntityId(int entityType, int entityId);
}
