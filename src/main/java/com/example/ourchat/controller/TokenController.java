package com.example.ourchat.controller;

import com.example.ourchat.constant.JWtClaimsConstant;
import com.example.ourchat.property.JwtProperties;
import com.example.ourchat.result.Result;
import com.example.ourchat.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Slf4j
public class TokenController {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 刷新访问token
     */
    @PostMapping("/refresh")
    public Result<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return Result.error("Refresh token不能为空");
        }

        try {
            // 验证refresh token
            if (!JwtUtil.validateRefreshToken(jwtProperties.getUserSecretKey(), refreshToken)) {
                return Result.error("Refresh token无效或已过期");
            }

            // 解析refresh token获取用户信息
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), refreshToken);
            Long userId = JwtUtil.getUserIdFromClaims(claims);

            // 生成新的访问token
            Map<String, Object> newClaims = new HashMap<>();
            newClaims.put(JWtClaimsConstant.USER_ID, userId);

            String newAccessToken = JwtUtil.createJWT(
                    jwtProperties.getUserSecretKey(),
                    jwtProperties.getUserTtl(),
                    newClaims
            );

            // 生成新的refresh token（可选，提高安全性）
            String newRefreshToken = JwtUtil.createRefreshJWT(
                    jwtProperties.getUserSecretKey(),
                    jwtProperties.getUserTtl() * 7, // refresh token有效期为访问token的7倍
                    newClaims
            );

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", newAccessToken);
            tokens.put("refreshToken", newRefreshToken);

            log.info("Token刷新成功，用户ID: {}", userId);
            return Result.success(tokens);

        } catch (Exception e) {
            log.error("Token刷新失败: {}", e.getMessage());
            return Result.error("Token刷新失败");
        }
    }
}