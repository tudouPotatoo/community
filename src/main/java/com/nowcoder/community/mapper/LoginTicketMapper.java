package com.nowcoder.community.mapper;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoginTicketMapper {
    /**
     * 插入一条LoginTicket数据
     * @param loginTicket
     * @return 插入的数据条数
     */
    int insert(LoginTicket loginTicket);

    /**
     * 根据登陆凭证进行查询
     * @param ticket
     * @return 查询到的LoginTicket数据
     */
    LoginTicket selectByTicket(String ticket);

    /**
     * 修改ticket对应的LoginTicket的status
     * @param ticket
     * @param status
     * @return 更新的记录条数
     */
    int updateStatus(@Param("ticket") String ticket, @Param("status") int status);
}
