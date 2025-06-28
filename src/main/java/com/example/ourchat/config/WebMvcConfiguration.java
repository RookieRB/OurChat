package com.example.ourchat.config;

import com.example.ourchat.interceptor.JWTTokenAdminInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Slf4j
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
    @Autowired
    private JWTTokenAdminInterceptor jwtTokenAdminInterceptor;

    protected void addInterceptors(InterceptorRegistry registry){
        log.info("开始注册自定义拦截器.......");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/user/**")
                .addPathPatterns("/chat/**")
                .addPathPatterns("/newFriends/**")
                .excludePathPatterns("/user/login/**")
                .excludePathPatterns("/auth/refresh"); // 排除token刷新端点
    }


}
