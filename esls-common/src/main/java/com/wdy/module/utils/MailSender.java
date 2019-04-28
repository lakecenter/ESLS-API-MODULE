package com.wdy.module.utils;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

@Component
public class MailSender {
    private static String FROM = "13058142866@163.com";

    //邮件发送的对象，用于邮件发送
    @Async
    public void sendMail(String to, String subject, String content, boolean isHtml) throws MessagingException {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.163.com");
        javaMailSender.setProtocol("smtp");
        javaMailSender.setDefaultEncoding("UTF-8");
        javaMailSender.setUsername(FROM);
        javaMailSender.setPassword("wcm031076216");
        //创建一个简单邮件信息对象
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(FROM);
        helper.setTo(to);
        helper.setSubject(subject);
        //第二个参数表明这是一个HTML
        helper.setText(content, true);
        helper.setSentDate(new Date(System.currentTimeMillis()));
        javaMailSender.send(message);
    }
}
