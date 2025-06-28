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
import java.util.HashMap;
import java.util.Map;

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
        log.info("当前token{}",token);
        // 校验token
        try{

            Claims claims =  JwtUtil.parseJWT(jwtProperties.getUserSecretKey(),token);
            Long userId = Long.valueOf(claims.get(JWtClaimsConstant.USER_ID).toString());
            Date exp = claims.getExpiration();

            if(exp.before(new Date())){
                // token失效
                throw new LoginInfoExpireException(MessageConstant.LOGIN_INFO_EXPIRE);
            }

            // 检查token是否即将过期，如果是则生成新token
            if(JwtUtil.isTokenNearExpiry(jwtProperties.getUserSecretKey(), token)) {
                log.info("Token即将过期，为用户{}生成新token", userId);

                // 生成新的访问token
                Map<String, Object> newClaims = new HashMap<>();
                newClaims.put(JWtClaimsConstant.USER_ID, userId);

                String newAccessToken = JwtUtil.createJWT(
                        jwtProperties.getUserSecretKey(),
                        jwtProperties.getUserTtl(),
                        newClaims
                );

                // 在响应头中返回新token
                response.setHeader("New-Access-Token", newAccessToken);
                response.setHeader("Token-Refreshed", "true");

                log.info("新token已生成并添加到响应头中");
            }

            BaseContext.setCurrentId(userId);
            log.info("验证成功，当前用户{}",userId);
            return  true;
        }catch (Exception e){
            response.setStatus(401);
            return false;
        }

    }
}
