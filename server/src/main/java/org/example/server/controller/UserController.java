package org.example.server.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.example.common.properties.EmailProperties;
import org.example.common.result.Result;
import org.example.common.utils.EmailOperatorRecognizer;
import org.example.common.utils.VerificationCodeUtil;
import org.example.common.utils.emailUtil;
import org.example.pojo.dto.UserInfoDTO;
import org.example.pojo.dto.UserRegistDTO;
import org.example.pojo.dto.UserGetCodeDTO;
import org.example.pojo.envcontent.AttentionListDataVo;
import org.example.pojo.envcontent.UserDataVo;
import org.example.pojo.user.userLogin;

import org.example.server.sever.ShopService;
import org.example.server.sever.UserService;
import org.example.server.sever.serviceImpl.UserDetailedInformationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.mail.Session;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户控制器类，处理用户相关的HTTP请求
 * 使用@RestController注解标记为RESTful控制器
 * 使用@RequiredArgsConstructor注解实现依赖注入
 * 使用@RequestMapping注解指定基础请求路径为"/user"
 * 使用@Slf4j注解实现日志功能
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {

    // JWT属性配置（已注释）
//    private final JwtProperties jwtProperties;
    /**
     * 用户详细信息服务实现类
     * 用于处理用户详细信息的业务逻辑
     */
   private final UserDetailedInformationServiceImpl userDetailedInformationService;
    /**
     * 用户服务实现类
     * 用于处理用户相关的业务逻辑
     */
    private final UserService userServiceImpl;
    /**
     * Redis模板
     * 用于操作Redis数据库
     */
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 会话映射
     * 存储用户会话信息的映射表
     */
    @Autowired
    private Map<String, Session> sessionMap;
    /**
     * 邮件属性配置
     * 用于配置邮件发送相关参数
     */
    @Autowired
    private EmailProperties emailProperties;
    @Autowired
    private ShopService shopService;
    @PostMapping("/login")
    public Result login(@RequestBody userLogin login) throws JsonProcessingException {
        String token = userServiceImpl.login(login);
        return Result.success(token);
    }

    /**
     * 更新用户信息接口
     * @param userInfoDTO 用户信息传输对象
     * @return 返回更新结果
     */
    @PostMapping(value="/userupdate",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result updateUserInfo(@ModelAttribute UserInfoDTO userInfoDTO) {
        // 处理更新用户信息的逻辑

            return userDetailedInformationService.updateUserInfo(userInfoDTO);

    }
    @GetMapping("/userInfo")
    public Result getUserInfo(@RequestParam("youxiang") String youxiang) {
        log.info("获取用户信息{}",youxiang);
        System.out.println(youxiang);
        return userDetailedInformationService.getUserInfo(youxiang);
    }

    @PostMapping("/register")
    // 使用@PostMapping注解，表示这是一个处理HTTP POST请求的方法
    public Result<Object> regist(@RequestBody UserRegistDTO dto) {  // 定义regist方法，接收userRegistDTO类型的参数，返回Result<Object>类型的结果

        // 调用userServiceimpl的existsByIdAndRegist方法处理注册逻辑
        return userServiceImpl.existsByIdAndRegist(dto);  // 调用userServiceimpl的existsByIdAndRegist方法处理注册逻辑并返回结果
    }
    @PostMapping("/getCode")
    public Result getCode(@RequestBody UserGetCodeDTO xxx) {
        // 生成6位验证码
        String code = VerificationCodeUtil.generateVerificationCode();
        String s = EmailOperatorRecognizer.recognizeOperator(xxx.getYouxiang());
        System.out.println(s);
        // 验证码缓存到redis中，key为手机号，value为验证码
        redisTemplate.opsForValue().set(xxx.getYouxiang() ,code,60, TimeUnit.SECONDS);
        //发送验证码
        Session session = sessionMap.get(s);
        String myCode = emailProperties.getSenders().get(s).getUsername();
        System.out.println(myCode);
        emailUtil.sent(code, xxx.getYouxiang(),session, myCode  );
        return Result.success("验证码发送成功");
    }
//    @PostMapping("/deleteShop")
//    public Result deleteShop(@RequestBody @JsonProperty("shopId") Long shopId) {
//        return userDetailedInformationService.deleteShop(shopId);
//    }


@GetMapping("/followslist")
    public Result<List<AttentionListDataVo>> followList(@RequestParam("userId") Long userId) {
        return userServiceImpl.followList(userId);
    }
    @GetMapping("/greenleafandfollows")
    public Result<UserDataVo> greenleafandfollows(@RequestParam("userId") Long userId) {
        return userServiceImpl.unfollow(userId);
    }
    @GetMapping("/follows")
    public  Result follow(@RequestParam("userId") Long userId){
        return userServiceImpl.follow(userId);
    }
}
