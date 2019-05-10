package com.wdy.module.utils;

import com.wdy.module.common.constant.MailConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.Security;
import java.util.Date;
import java.util.Properties;

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

    @Async
    public void sendSSLMail(String to, String subject, String content) throws Exception {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        // Get a Properties object
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", mailConstant.getHOST());
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.auth", "true");
        final String username = mailConstant.getFROM();
        final String password = mailConstant.getPASSWORD();
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        //创建一个简单邮件信息对象
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(username));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        msg.setContent(content, "text/html;charset=utf-8");
        msg.setSentDate(new Date(System.currentTimeMillis()));
        Transport.send(msg);
    }
}
