package org.example.server.config;

import org.example.common.properties.EmailProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class emailregistconfig {
    @Autowired
    private EmailProperties emailProperties;
    @Bean
    public Map<String, Session> sessionMap(){
        Map<String, Session> sessionMap = new HashMap<>();
        // 邮箱SMTP服务器设置
        Map<String, EmailProperties.SenderConfig> senders=emailProperties.getSenders();
        for(Map.Entry<String, EmailProperties.SenderConfig> sender : senders.entrySet()){
            String key = sender.getKey();
            EmailProperties.SenderConfig value = sender.getValue();
            if(value.getStarttlsEnable() ==true){
                // 邮箱SMTP服务器设置
                Properties props = new Properties();
                props.put("mail.smtp.host", value.getHost());
                props.put("mail.smtp.port", value.getPort()); // 或465
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", value.getStarttlsEnable()); // 使用STARTTLS安全连接 // 如果使用SSL，取消上面这行，改为： // props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // props.put("mail.smtp.socketFactory.port", "465");
                //创建Session对象
                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(value.getUsername(), value.getPassword());
                    }
                });
                sessionMap.put(key, session);
            }else{
                Properties props = new Properties();
                props.put("mail.smtp.host", value.getHost());
                props.put("mail.smtp.port", value.getPort()); // 或465
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.port", value.getPort());
                props.put("mail.smtp.socketFactory.fallback", "false");//不允许回退到非加密连接
                //创建Session对象
                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(value.getUsername(), value.getPassword());
                    }
                });

                sessionMap.put(key, session);

            }


        }
        return sessionMap;
    }
}
