package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.mapper.LoginTicketMapper;
import com.nowcoder.community.mapper.UserMapper;
import com.nowcoder.community.utils.CookieUtil;
import com.nowcoder.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从cookie中获取loginTicket的ticket值
        String ticket = CookieUtil.getCookie(request, "loginTicket");
        // 2. 根据ticket查找对应的loginTicket对象
        LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
        // 3. 校验loginTicket是否合法（是否不为null，是否有效，是否未过期），如果合法继续往下
        if (loginTicket != null && loginTicket.getStatus() == 1 && loginTicket.getExpired().after(new Date())) {
            // 4. 根据loginTicket的值获取user信息
            User user = userMapper.selectById(loginTicket.getUserId());
            // 5. 用HostHolder将当前线程所关联的user对象存储起来
            hostHolder.setUser(user);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        // 从HostHolder中获取user对象，放入ModelAndView对象中
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 将HostHolder中存储的user对象清除
        hostHolder.remove();
    }
}
