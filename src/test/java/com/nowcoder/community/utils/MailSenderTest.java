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
    @Autowired
    private EmailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    /***
     * 测试发送邮件
     */
    @Test
    public void testSendMail() {
        String to = "alicelai435@gmail.com";
        String subject = "你好呀111";
        String content = "<html><h1>我是小白</h1></html>";
        mailSender.sendMail(to, subject, content);
    }

    /**
     * 使用模板引擎渲染后，将模板内容作为content值发送邮件
     */
    @Test
    public void testSendHtmlMail() {
        Context context = new Context();
        // 将demo.html中的username变量设置值为aaa
        context.setVariable("username", "aaa");
        // 进行渲染
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        // 发送邮件
        String to = "alicelai435@gmail.com";
        String subject = "你好呀111";
        mailSender.sendMail(to, subject, content);
    }
}
