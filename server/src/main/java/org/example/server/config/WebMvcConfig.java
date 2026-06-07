package org.example.server.config;

import org.example.server.interceptor.AllinterCepter;
import org.example.server.interceptor.LoginIntercepto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
   @Autowired
   private AllinterCepter allinterCepter;

    @Autowired
    private LoginIntercepto loginInterceptor;

    //这里拦截器用注入的，之前是直接new一个interCepter,但是这样会导致JwtProperties没有注入，所以这里用注入的
    //就是这里用注入会导致interCepter里面的JwtProperties不能注入
    //用注入就都用注入
    @Override
    public void addInterceptors(InterceptorRegistry registry) {


        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login/**","/ai/chat/stream/**","/valuation/**","/user/register/**","/user/getCode/**","/envContent/**","/error","/ws/**")
                .order(1);


        registry.addInterceptor(allinterCepter)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login/**","/ai/chat/stream/**","/valuation/**","/user/register/**","/user/getCode/**","/envContent/**","/error","/ws/**")
                .order(0);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 上线后通常是不同域名/端口，这里放开让前端能携带自定义请求头（如 token）
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("token", "Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }
}