package org.example.server.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.example.common.properties.JwtProperties;
import org.example.common.utils.UserHolder;

import org.example.pojo.dto.UserDTO;
import org.example.pojo.envcontent.TaskItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.example.common.constant.RedisConstants.LOGIN_TOKEN;


@Slf4j
@Component
public class AllinterCepter implements HandlerInterceptor {


    private StringRedisTemplate stringRedisTemplate;
    @Autowired
  private ObjectMapper objectMapper;
    public AllinterCepter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = extractToken(request);
        if (StrUtil.isBlank(token)) {
            // 没有token，返回错误
            log.debug("token为空, method={}, uri={}", request.getMethod(), request.getRequestURI());
            return true;
        }
        //基于Token在redis中获取用户信息
    String userJson = stringRedisTemplate.opsForValue().get(LOGIN_TOKEN+token) ;
        if (StrUtil.isBlank(userJson)) {
            log.debug("token无效/用户不存在, method={}, uri={}", request.getMethod(), request.getRequestURI());
            return true; // token无效
        }
        UserDTO userDTO = objectMapper.readValue(userJson,
                new TypeReference<UserDTO>() {
                });
        UserHolder.saveUser(userDTO);
        //刷新有效期
        stringRedisTemplate.expire(LOGIN_TOKEN+token, 30, TimeUnit.MINUTES);
        System.out.println("刷新有效期");

        return true;
    }

    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StrUtil.isNotBlank(token)) {
            return token;
        }
        // 兼容常见写法：Authorization: Bearer <token>
        String authorization = request.getHeader("Authorization");
        if (StrUtil.isBlank(authorization)) {
            authorization = request.getHeader("authorization");
        }
        if (StrUtil.isBlank(authorization)) {
            return null;
        }
        String prefix = "Bearer ";
        if (authorization.regionMatches(true, 0, prefix, 0, prefix.length())) {
            return authorization.substring(prefix.length()).trim();
        }
        return authorization.trim();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }
}