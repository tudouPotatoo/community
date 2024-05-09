package com.nowcoder.community.controller.adivice;

import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@ControllerAdvice(annotations = Controller.class)  // 只对添加了Controller注解的类的异常进行捕获
public class ExceptionAdvice implements CommunityConstant {

    /**
     * 捕获Exception类型的异常
     * 1. 记录异常日志
     * 2. 判断请求类型
     *    2.1 异步请求 返回json类型返回值
     *    2.2 同步请求 重定向到500页面
     * @param e
     * @param request
     * @param response
     */
    @ExceptionHandler(value = Exception.class)
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. 记录异常日志
        log.error(e.getMessage());
        // 日志详细信息
        for (StackTraceElement element :e.getStackTrace()) {
            log.error(element.toString());
        }
        // 2. 判断请求类型
        String requestType = request.getHeader("x-requested-with");
        // 2.1 异步请求 返回json类型返回值
        if ("XMLHttpRequest".equalsIgnoreCase(requestType)) {
            // 设置返回值类型
            response.setContentType("application/plain;charset=utf-8");
            // 将返回值写入response
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJsonString(SERVER_FAIL, "服务器异常，请稍后重试..."));
        } else {
            // 2.2 同步请求 重定向到500页面
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
