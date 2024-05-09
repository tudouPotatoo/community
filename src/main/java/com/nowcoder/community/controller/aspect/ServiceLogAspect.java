package com.nowcoder.community.controller.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
@Aspect
public class ServiceLogAspect {

    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut() {

    }

    /**
     * 在访问Service层的业务方法时记录日志
     * 日志格式：用户[127.0.0.1]在[2024.05.09 20:36:23]访问了[com.nowcoder.community.service.CommentService.getCommentByEntityId()].
     * 1. 获取请求ip
     * 2. 获取当前时间
     * 3. 获取访问的方法
     * 4. 记录日志
     */
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        //  1. 获取请求ip
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String ip = request.getRemoteHost();
        // 2. 获取当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = formatter.format(new Date());
        // 3. 获取访问的方法
        String method = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        // 4. 记录日志
        log.info(String.format("用户[%s]在[%s]访问了[%s].", ip, now, method));
    }
}
