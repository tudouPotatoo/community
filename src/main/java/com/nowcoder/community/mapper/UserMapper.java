package com.nowcoder.community.mapper;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User selectById(int id);

    User selectByUsername(String username);

    User selectByEmail(String email);

    int insert(User user);

    int updateStatus(@Param("id") int userId, @Param("status") int status);

    int insert(@Param("username") String username, @Param("password") String password, @Param("email") String email);

    int updateHeaderUrl(@Param("id") int userId, @Param("headerUrl") String headerUrl);
}
