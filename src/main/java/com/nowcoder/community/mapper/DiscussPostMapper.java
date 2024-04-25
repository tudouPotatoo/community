package com.nowcoder.community.mapper;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 添加@Mapper注解，MyBatis才会为接口生成对应的实现类
 */
@Mapper
public interface DiscussPostMapper {

    /**
     * 当userId = 0时，用于获取该分页下所有帖子
     * 当userId > 0时，用于获取对应用户该分页下所有帖子
     * @param userId 用户id
     * @param offset 分页的起始行
     * @param pageSize 分页的大小
     * @return 查询到的帖子
     */
    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 获取帖子的总数量 便于进行分页
     * 当userId = 0时，用于获取所有帖子的总数量
     * 当userId > 0时，用于获取对应用户的所有帖子的总数量
     * @param userId 用户Id
     * @return 帖子总个数
     */
    int selectDiscussPostRows(int userId);

    /**
     * 插入一条帖子
     * @param discussPost
     * @return
     */
    int insertDiscussPost(DiscussPost discussPost);
}
