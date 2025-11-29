package com.itmk.config;

import com.anji.captcha.model.common.Const;
import com.anji.captcha.service.CaptchaService;
import com.anji.captcha.service.impl.CaptchaServiceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class CaptchaConfig {

    @Bean
    public CaptchaService captchaService() {
        Properties config = new Properties();

        // 1. 缓存类型：local (内存)
        config.put(Const.CAPTCHA_CACHETYPE, "local");

        // 2. 水印
        config.put(Const.CAPTCHA_WATER_MARK, "挂号系统");

        // 3. 字体
        config.put(Const.CAPTCHA_FONT_TYPE, "宋体");

        // 4. 滑动干扰
        config.put(Const.CAPTCHA_SLIP_OFFSET, "5");

        // 5. 开启 AES (配合前端)
        config.put(Const.CAPTCHA_AES_STATUS, "true");

        // 使用 Jar 包里的工厂类创建实例
        return CaptchaServiceFactory.getInstance(config);
    }
}