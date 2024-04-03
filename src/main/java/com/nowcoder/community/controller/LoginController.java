package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    /**
     * 获取注册页面
     * @return 返回注册页面路径
     */
    @GetMapping(path = "register")
    public String getRegisterPage() {
        return "/site/register";
    }

    /**
     * 获取登录页面
     * @return 返回登录页面路径
     */
    @GetMapping(path = "login")
    public String getLoginPage() {
        return "/site/login";
    }

    /**
     * 注册功能
     * @param mv
     * @param user
     * @return
     */
    @PostMapping("/register")
    public ModelAndView register(ModelAndView mv, User user) {
        Map<String, Object> map = userService.register(user);
        // 如果注册成功 跳转到首页
        if (map == null || map.isEmpty()) {
            mv.addObject("msg", "注册成功，我们已向您的注册邮箱发送了一封激活邮件，请尽快激活！");
            mv.addObject("target", "/index");
            mv.setViewName("/site/operate-result");
        } else {
            // 如果注册失败 停留在当前页面
            mv.addObject("usernameMsg", map.get("usernameMsg"));
            mv.addObject("passwordMsg", map.get("passwordMsg"));
            mv.addObject("emailMsg", map.get("emailMsg"));
            mv.setViewName("/site/register");
        }
        return mv;
    }

    /**
     * 激活账号
     * 激活链接：http://localhost:80/community/register/用户id/激活码
     * @param mv
     * @return
     */
    @GetMapping("/register/{userId}/{activationCode}")
    public ModelAndView activate(ModelAndView mv, @PathVariable("userId") Integer userId, @PathVariable("activationCode") String activationCode) {
        System.out.println("userId:" + userId);
        System.out.println("activationCode:" + activationCode);
        int result = userService.activate(userId, activationCode);
        if (result == CommunityConstant.ACTIVATION_SUCCESS) {
            mv.addObject("msg", "您的账号已被成功激活！");
            mv.addObject("target", "/login");
        } else if (result == CommunityConstant.ACTIVATION_REPEAT) {
            mv.addObject("msg", "您的账号已被激活，请勿重复激活！");
            mv.addObject("target", "/index");
        } else {
            mv.addObject("msg", "您提供的激活码无效");
            mv.addObject("target", "/index");
        }
        mv.setViewName("/site/operate-result");
        return mv;
    }
}
