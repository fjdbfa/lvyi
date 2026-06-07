package org.example.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.properties.EmailProperties;
import org.example.common.result.Result;
import org.example.common.utils.EmailOperatorRecognizer;
import org.example.common.utils.VerificationCodeUtil;
import org.example.common.utils.emailUtil;
import org.example.pojo.dto.UserRegistDTO;
import org.example.pojo.dto.UserGetCodeDTO;
import org.example.server.sever.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.Session;
import java.util.Map;
import java.util.concurrent.TimeUnit;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/forgetPassword")
public class forgetPassword {
    private final UserService userServiceimpl;
    private final RedisTemplate redisTemplate;
    private final EmailProperties emailProperties;
    private final Map<String, Session> sessionMap;

    @RequestMapping(value = "/getCode", method = RequestMethod.POST)
    public Result getCode(@RequestBody UserGetCodeDTO xxx) {
        String code = VerificationCodeUtil.generateVerificationCode();

        String s = EmailOperatorRecognizer.recognizeOperator(xxx.getYouxiang());
        System.out.println(s);
        // 验证码缓存到redis中，key为手机号，value为验证码
        redisTemplate.opsForValue().set("updatePassword"+xxx.getYouxiang(), code, 60, TimeUnit.SECONDS);
        //发送验证码
        Session session = sessionMap.get(s);
        String myCode = emailProperties.getSenders().get(s).getUsername();
        System.out.println(myCode);
        emailUtil.sent(code, xxx.getYouxiang(), session, myCode);
        return Result.success("验证码发送成功");

    }
    @RequestMapping()
    public Result updatePassword(@RequestBody UserRegistDTO dto){
        return userServiceimpl.updatePassword(dto);

    }
}
