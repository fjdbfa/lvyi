package org.example.server.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.common.exception.BusinessException;
import org.example.common.utils.UserHolder;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginIntercepto implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        // 跨域预检请求直接放行，否则浏览器实际请求发不出去
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        //判断是否需要拦截(ThreadLocal中是否有用户)
        if(UserHolder.getUser() == null) {
            System.out.println("未登录");
            throw new BusinessException("未登录", 401, HttpStatus.UNAUTHORIZED);
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        }
        return true;
    }
}
