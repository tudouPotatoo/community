package com.nowcoder.community.mapper;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectById(int id);
}
