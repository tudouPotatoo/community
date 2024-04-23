package com.nowcoder.community.service;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.mapper.LoginTicketMapper;
import com.nowcoder.community.mapper.UserMapper;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.EmailSender;
import com.nowcoder.community.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

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
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
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
        // System.out.println("activationLink:" + activationLink);

        String process = templateEngine.process("/mail/activation", context);
        emailSender.sendMail(user.getEmail(), "激活邮箱", process);
        return registerInfo;
    }

    /**
     * 激活账户
     * 1. 检查userId是否合法
     * 2. 根据userId获取用户user信息
     * 3. 检查user激活状态
     *    3.1 已经激活过 --> 返回重复激活信息
     *    3.2 没有激活过 检查激活码是否正确
     *            不正确 --> 返回激活失败信息
     *            正确   --> 激活用户 返回激活成功信息
     * @param userId 用户id
     * @param activationCode 激活码
     * @return 返回激活状态
     */
    public int activate(Integer userId, String activationCode) {
        // 1. 检查userId是否合法
        if (userId == null) {
            return CommunityConstant.ACTIVATION_FAILURE;
        }
        // 2. 根据userId获取用户user信息
        User user = userMapper.selectById(userId);

        // 3. 检查user激活状态
        // 3.1 已经激活过 --> 返回重复激活信息
        if (user.getStatus() == 1) {
            return CommunityConstant.ACTIVATION_REPEAT;
            // 3.2 没有激活过 检查激活码是否正确
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

    /*
     * 用户登陆
     * 1. 验证username passwords是否为空
     * 2. 验证username是否存在
     * 3. 验证password是否正确
     *    上面1-3通过之后说明用户信息无误，可以进行登陆
     * 4. 生成登陆凭证 插入数据库 返回LoginTicket的ticket信息
     *    （返回到Controller层将ticket信息添加到Cookie，让用户能够获得ticket并且每次访问都携带ticket，实现维持登陆状态）
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> loginInfo = new HashMap<>();
        // 1. 验证username passwords是否为空
        if (StringUtils.isBlank(username)) {
            loginInfo.put("usernameMsg", "用户名不能为空！");
            return loginInfo;
        }
        if (StringUtils.isBlank(password)) {
            loginInfo.put("passwordMsg", "密码不能为空！");
            return loginInfo;
        }
        // 2. 验证username是否存在
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            loginInfo.put("usernameMsg", "该用户不存在！");
            return loginInfo;
        }
        // 3. 验证password是否正确
        if (!CommunityUtil.md5(password + user.getSalt()).equals(user.getPassword())) {
            loginInfo.put("passwordMsg", "密码错误！");
            return loginInfo;
        }
        // 上面1-3通过之后说明用户信息无误，可以进行登陆
        // 生成登陆凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(1);
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        // 插入数据库
        loginTicketMapper.insert(loginTicket);
        // 返回LoginTicket的ticket信息
        loginInfo.put("loginTicket", loginTicket.getTicket());
        return loginInfo;
    }

    /**
     * 退出登陆
     * 将ticket所对应的LoginTicket登陆凭证设为无效
     * @param ticket
     */
    public void logout(String ticket) {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
        if (loginTicket != null) {
            // 将登陆凭证设为无效（0）
            loginTicketMapper.updateStatus(ticket, 0);
        }
    }

    /**
     * 修改用户的头像
     * @param userId
     * @param headerUrl
     * @return
     */
    public int updateHeader(Integer userId, String headerUrl) {
        return userMapper.updateHeaderUrl(userId, headerUrl);
    }

    /**
     * 更新用户密码
     * 1. 从ThreadLocal中获取当前登录用户信息
     * 2. 校验原始密码是否正确
     *    错误 --> 返回密码错误信息
     *    正确 --> 继续往下
     * 3. 修改密码为新密码
     * @param oldPassword
     * @param newPassword
     * @return
     */
    public Map<String,String> updatePassword(String oldPassword, String newPassword) {
        Map<String, String> info = new HashMap<>();
        // 1. 从ThreadLocal中获取当前登录用户信息
        User user = hostHolder.getUser();
        Integer userId = user.getId();
        String salt = user.getSalt();
        String password = user.getPassword();
        // 2. 校验原始密码是否正确
        oldPassword = CommunityUtil.md5(oldPassword + salt);
        // 错误 --> 返回密码错误信息
        if (!oldPassword.equals(password)) {
            info.put("oldPasswordMsg", "密码错误！");
            return info;
        }
        // 正确 --> 继续往下

        // 3. 修改密码为新密码
        newPassword = CommunityUtil.md5(newPassword + salt);
        userMapper.updatePassword(userId, newPassword);
       return info;
    }
}
