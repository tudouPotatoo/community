package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String CONTEXT_PATH;

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
        // System.out.println("userId:" + userId);
        // System.out.println("activationCode:" + activationCode);
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

    /**
     * 生成验证码
     * 1. 生成验证码图片并且写入response中
     * 2. 将验证码字符加入session 以便与用户输入进行比较
     * @param response
     * @param session
     */
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        // 生成验证码文本
        String text = kaptchaProducer.createText();
        // 根据文本生成验证码图片
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码图片写入response中
        response.setContentType("image/png");
        try {
            ServletOutputStream os = response.getOutputStream();
            // 用ImageIO将图片写到response对象中，指定输出的图片、格式、流
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            log.error("生成验证码出错：", e.getMessage());
        }

        // 将验证码字符加入session
        session.setAttribute("kaptcha", text);
    }

    /**
     * 用户登陆
     * 1. 验证【验证码】是否正确
     *    1.1 如果错误 mv中添加验证码错误信息 跳转到login页面
     *    1.2 如果正确 继续往下执行验证用户名和密码的逻辑
     * 2. 调用service层实现登陆
     * 3. 验证是否登陆成功
     *    3.1 登陆失败 mv中添加用户名/密码错误信息 跳转到login页面
     *    3.2 登陆成功 把生成的登陆凭证LoginTicket放入cookie 返回给用户
     *                跳转到主页
     * @param username 用户名
     * @param password 密码
     * @param kaptcha 验证码
     * @param session session中存储了验证码值，用于验证用户输入的kaptcha是否正确
     * @param response 生成的LoginTicket需要写入Cookie，放入response中传给用户
     * @param mv ModelAndView对象 携带参数 实现页面跳转
     */
    @PostMapping("/login")
    public ModelAndView login(String username, String password, String kaptcha, boolean rememberMe,
                      HttpSession session, HttpServletResponse response, ModelAndView mv) {
        // 1. 验证【验证码】是否正确
        String correctKaptcha = (String) session.getAttribute("kaptcha");
        if (StringUtils.isBlank(correctKaptcha) || StringUtils.isBlank(kaptcha) || !kaptcha.equalsIgnoreCase(correctKaptcha)) {
            // 1.1 如果错误 mv中添加验证码错误信息 跳转到login页面
            mv.addObject("kaptchaMsg", "验证码错误，请重新输入！");
            mv.setViewName("/site/login");
            return mv;
        }

        // 2. 调用service层实现登陆
        // 根据是否勾选【记住我】设置登陆凭证有效时长
        int expiredSeconds = 0;
        if (rememberMe) {
            expiredSeconds = CommunityConstant.REMEMBER_ME_EXPIRED_SECONDS;
        } else {
            expiredSeconds = CommunityConstant.DEFAULT_EXPIRED_SECONDS;
        }
        Map<String, Object> loginResult = userService.login(username, password, expiredSeconds);

        // 3. 验证是否登陆成功
        if (loginResult.containsKey("loginTicket")) {
            // 3.2 登陆成功 把生成的登陆凭证LoginTicket放入cookie 返回给用户
            Cookie cookie = new Cookie("loginTicket", loginResult.get("loginTicket").toString());
            // 设置cookie有效范围
            cookie.setPath(CONTEXT_PATH);
            // 设置cookie有效时长（单位：秒）
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            // 跳转到主页
            mv.setViewName("redirect:/index");
        } else {
            // 3.1 登陆失败 mv中添加用户名/密码错误信息 跳转到login页面
            mv.addObject("usernameMsg", loginResult.get("usernameMsg"));
            mv.addObject("passwordMsg", loginResult.get("passwordMsg"));
            mv.setViewName("/site/login");
        }
        return mv;
    }

    /**
     * 退出登陆
     * @param ticket Cookie中的登陆凭证
     * @param mv ModelAndView对象
     * @return
     */
    @GetMapping("/logout")
    public ModelAndView logout(@CookieValue("loginTicket") String ticket, ModelAndView mv) {
        userService.logout(ticket);
        mv.setViewName("redirect:/login");
        return mv;
    }
}
