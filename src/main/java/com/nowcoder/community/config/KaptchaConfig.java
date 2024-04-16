package com.nowcoder.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Properties;

/**
 * kaptcha配置类，spring会在启动应用时加载它
 */
@Configuration
public class KaptchaConfig {

    /**
     * 生成一个验证码生成器bean，加入spring容器，让spring来管理
     * @return 生成的验证码生成器【Producer是DefaultKaptcha的接口类型】
     */
    // @Bean这个注解告诉Spring容器，该方法将返回一个bean，并且这个bean的实例将由Spring容器管理。
    @Bean
    public Producer kaptchaProducer() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "100");
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");

        // 创建一个默认验证码生成器
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        // 将上述配置都应用到验证码生成器上
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
