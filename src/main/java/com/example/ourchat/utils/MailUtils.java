package com.example.ourchat.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class MailUtils {
    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.host}")
    private String host;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    public void sendSimpleMail(String to, String subject, String code) throws Exception {
        Properties prop = new Properties();
        prop.setProperty("mail.host", host);
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.auth", "true");
        // 使用javaMail发送邮件的5个步骤
        // 1.创建session
        Session session = Session.getInstance(prop);
        // 开启Session的debug模式,这样就可以查看到程序发送Email的运行状态
        session.setDebug(true);
        // 2.通过session得到transport对象
        Transport ts = session.getTransport();
        // 3.使用邮箱的用户名和密码连接上邮箱服务器,发送邮件时,发件人邮箱必须与认证用户一致
        ts.connect(host, username, password);
        // 4.创建邮件
        MimeMessage message = new MimeMessage(session);
        // 指明邮件的发件人

        message.setFrom(new InternetAddress(username));
        // 指明邮件的收件人
        message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(to));
        // 邮件的标题
        message.setSubject(subject);

        // 邮件的文本内容
        message.setContent("<p style='color:red'>尊敬的用户您好！！！您的验证码为：" + code +"(有效期为一分钟)</p>","text/html;charset=UTF-8");
        // 将邮件与code 传入redis 中 并设置有效时间为一分钟
        redisTemplate.opsForValue().set(to, code, Duration.ofMinutes(1));
        // 5.发送邮件
        ts.sendMessage(message, message.getAllRecipients());
        // 6.关闭连接
        ts.close();
    }

}
