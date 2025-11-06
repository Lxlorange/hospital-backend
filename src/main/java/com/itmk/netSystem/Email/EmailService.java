package com.itmk.netSystem.Email; // 替换为您的包名

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    // 从配置文件中获取发件人邮箱
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送简单的文本邮件 (异步执行)
     * @param to      收件人地址
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    @Async // 标记为异步方法
    public void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        try {
            mailSender.send(message);
            logger.info("邮件已成功发送至 {}", to);
        } catch (Exception e) {
            logger.error("发送邮件到 {} 时发生异常", to, e);
        }
    }
}