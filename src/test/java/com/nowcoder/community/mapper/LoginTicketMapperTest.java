package com.nowcoder.community.mapper;

import com.nowcoder.community.CommunityApplication;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.utils.CommunityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {CommunityApplication.class})
public class LoginTicketMapperTest {
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testInsert() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(157);
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        // 1000毫秒 = 1s --> 1000 * 60 = 1分钟 --> 1000 * 60 * 10 = 10分钟
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        loginTicketMapper.insert(loginTicket);
    }

    @Test
    public void testSelectByTicket() {
        String ticket = "cee541fa9a014f9c94046b4ea67454d8";
        LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
        System.out.println(loginTicket);
    }

    @Test
    public void testUpdateStatus() {
        String ticket = "cee541fa9a014f9c94046b4ea67454d8";
        loginTicketMapper.updateStatus(ticket, 1);
        System.out.println(loginTicketMapper.selectByTicket(ticket));
    }
}
