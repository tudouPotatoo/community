package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandle;

/**
 * 拦截所有需要登陆才能访问的路径
 * 如果未登陆 --> 拦截
 * 如果登陆  --> 放行
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    /**
     * 1. 查看拦截的是否是方法请求
     *      不是 --> 放行
     *       是  --> 继续往下
     * 2. 查看该方法是否包含@LoginRequired注解
     *      不是 --> 放行
     *        是 --> 继续往下
     * 3. 尝试从HostHolder中获取当前用户
     *       获取不到 --> 说明用户未登陆 拦截
     *       能够获取 --> 说明用户已登陆 放行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 查看拦截的是否是方法请求
        if (handler instanceof HandlerMethod) {
            // 2. 查看该方法是否包含@LoginRequired注解
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            LoginRequired loginRequired = handlerMethod.getMethodAnnotation(LoginRequired.class);
            // 如果包含@LoginRequired注解 且 用户未登录 -----> 拦截
            // 3. 尝试从HostHolder中获取当前用户
            if (loginRequired != null && hostHolder.getUser() == null) {
                // 跳转至登陆节点
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
