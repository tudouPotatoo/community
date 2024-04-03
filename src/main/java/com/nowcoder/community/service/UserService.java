package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.mapper.UserMapper;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.EmailSender;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private EmailSender emailSender;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.domain}")
    private String domain;

    public User getUserById(int id) {
        return userMapper.selectById(id);
    }

    /**
     * 用户注册
     * 1. 验证用户传入的信息是否合法
     * 2. 将用户信息补全（类似type、status等）
     * 3. 将用户数据插入数据库
     * 4. 向用户邮箱发送激活链接
     * @param user
     * @return 报错信息
     */
    public Map<String, Object> register(User user) {
        Map<String, Object> registerInfo = new HashMap<>();
        if (user == null) {
            registerInfo.put("usernameMsg", "用户名不能为空!");
            return registerInfo;
        }
        // 验证用户名是否为空
        if (StringUtils.isBlank(user.getUsername())) {
            registerInfo.put("usernameMsg",  "用户名不能为空!");
            return registerInfo;
        }
        // 验证密码是否为空
        if (StringUtils.isBlank(user.getPassword())) {
            registerInfo.put("passwordMsg",  "密码不能为空!");
            return registerInfo;
        }
        // 验证邮箱是否为空
        if (StringUtils.isBlank(user.getUsername())) {
            registerInfo.put("emailMsg",  "密码不能为空!");
            return registerInfo;
        }
        // 验证用户名是否重复
        User u = userMapper.selectByUsername(user.getUsername());
        if (u != null) {
            registerInfo.put("usernameMsg", "该用户名已存在!");
            return registerInfo;
        }
        // 验证邮箱是否重复
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            registerInfo.put("emailMsg", "该邮箱已被注册!");
            return registerInfo;
        }

        // 完善用户数据
        // salt
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        // password
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        // type 普通用户
        user.setType(0);
        // status 未激活
        user.setStatus(0);
        // activationCode
        user.setActivationCode(CommunityUtil.generateUUID());
        // headerUrl
        user.setHeaderUrl(String.format("images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        // createTime
        user.setCreateTime(new Date());

        // 将用户数据加入数据库
        // insert会将生成的id再赋值给user的id属性（配置文件中进行了配置）
        userMapper.insert(user);

        // 向用户邮箱发送激活链接
        Context context = new Context();

        context.setVariable("email", user.getEmail());
        // 激活链接：http://localhost:80/community/register/1(用户id)/激活码
        String activationLink = domain + contextPath + "/register/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", activationLink);
        System.out.println("activationLink:" + activationLink);

        String process = templateEngine.process("/mail/activation", context);
        emailSender.sendMail(user.getEmail(), "激活邮箱", process);
        return registerInfo;
    }

    /**
     * 激活账户
     * @param userId 用户id
     * @param activationCode 激活码
     * @return 返回激活状态
     */
    public int activate(Integer userId, String activationCode) {
        if (userId == null) {
            return CommunityConstant.ACTIVATION_FAILURE;
        }
        User user = userMapper.selectById(userId);
        // 当前用户已经激活过了 重复激活
        if (user.getStatus() == 1) {
            return CommunityConstant.ACTIVATION_REPEAT;
        } else {
            // 如果提供的激活码不正确 则激活失败
            if (!user.getActivationCode().equals(activationCode)) {
                return CommunityConstant.ACTIVATION_FAILURE;
            } else {
                // 之前未激活过 且激活码正确 则可以成功激活
                user.setStatus(1);
                userMapper.updateStatus(userId, 1);
                return CommunityConstant.ACTIVATION_SUCCESS;
            }
        }
    }


}
