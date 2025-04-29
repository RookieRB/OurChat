package com.example.ourchat.interceptor;

import com.example.ourchat.utils.JwtUtil;
import com.example.ourchat.constant.JWtClaimsConstant;
import com.example.ourchat.constant.MessageConstant;
import com.example.ourchat.context.BaseContext;
import com.example.ourchat.exception.LoginInfoExpireException;
import com.example.ourchat.property.JwtProperties;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Slf4j
@Component
public class JWTTokenAdminInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断当前拦截到的是不是Controller的方法
        if(!(handler instanceof HandlerMethod)){
            // 如果不是，直接放行
            return true;
        }

        // 获取请求头当中的token
        String token = request.getHeader(jwtProperties.getUserTokenName());

        // 校验token
        try{

            Claims claims =  JwtUtil.parseJWT(jwtProperties.getUserSecretKey(),token);
            Long userId = Long.valueOf(claims.get(JWtClaimsConstant.USER_ID).toString());
            Date exp = claims.getExpiration();

            if(exp.before(new Date())){
                // token失效
                throw new LoginInfoExpireException(MessageConstant.LOGIN_INFO_EXPIRE);
            }

            BaseContext.setCurrentId(userId);
            return  true;
        }catch (Exception e){
            response.setStatus(401);
            return false;
        }

    }
}
