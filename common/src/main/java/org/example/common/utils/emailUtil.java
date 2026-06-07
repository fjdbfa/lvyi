package org.example.common.utils;

import org.example.common.properties.EmailProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.Map;

@Component
public class emailUtil {

private Map<String,Session> sessionMap;
    public static void sent(String email, String codes, Session session,String myCode){
        try { // 创建邮件消息
            Message message = new MimeMessage(session );
            message.setFrom(new InternetAddress(myCode));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(codes));
            LocalDate localDate = LocalDate.now();

            message.setSubject("验证码");
            message.setText("验证码为"+":"+email+"\n"+"有效期为60秒");
            // 发送邮件
            Transport.send(message);
            System.out.println("验证码已发送至：" + codes);
        } catch (MessagingException e) {
            throw new RuntimeException("邮件发送失败", e);
        }

    }

}
