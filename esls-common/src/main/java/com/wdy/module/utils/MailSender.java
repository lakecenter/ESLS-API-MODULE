package com.wdy.module.utils;

import com.wdy.module.common.constant.MailConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

@Component
public class MailSender {
    @Autowired
    private MailConstant mailConstant;

    //邮件发送的对象，用于邮件发送
    @Async
    public void sendMail(String to, String subject, String content, boolean isHtml) throws MessagingException {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(mailConstant.getHOST());
        javaMailSender.setProtocol(mailConstant.getPROTOCOL());
        javaMailSender.setDefaultEncoding(mailConstant.getDEFAULTENCODING());
        javaMailSender.setUsername(mailConstant.getFROM());
        javaMailSender.setPassword(mailConstant.getPASSWORD());
        //创建一个简单邮件信息对象
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(mailConstant.getFROM());
        helper.setTo(to);
        helper.setSubject(subject);
        //第二个参数表明这是一个HTML
        helper.setText(content, isHtml);
        helper.setSentDate(new Date(System.currentTimeMillis()));
        javaMailSender.send(message);
    }
}
