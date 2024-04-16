package com.nowcoder.community.utils;

import com.nowcoder.community.CommunityApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {CommunityApplication.class})
public class MailSenderTest {
    /**
     * 负责发送邮件的bean
     */
    @Autowired
    private EmailSender mailSender;

    /**
     * 负责渲染模板的模板引擎bean
     */
    @Autowired
    private TemplateEngine templateEngine;

    /***
     * 测试发送邮件
     */
    @Test
    public void testSendMail() {
        String to = "alicelai435@gmail.com";
        String subject = "激活账号邮件";
        String content = "<html><h1>我是小白</h1></html>";
        mailSender.sendMail(to, subject, content);
    }

    /**
     * 使用模板引擎渲染后，将模板内容作为content值发送邮件
     */
    @Test
    public void testSendHtmlMail() {
        Context context = new Context();
        // 将demo.html中的username变量设置值为小白
        context.setVariable("username", "小白");
        // 使用模板引擎，对html模板进行渲染
        String content = templateEngine.process("/mail/demo", context);

        // 发送邮件
        String to = "alicelai435@gmail.com";
        String subject = "激活账号邮件";
        mailSender.sendMail(to, subject, content);
    }
}
