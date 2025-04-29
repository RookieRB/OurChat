package com.example.ourchat.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.example.ourchat.constant.JWtClaimsConstant;
import com.example.ourchat.context.BaseContext;
import com.example.ourchat.property.JwtProperties;
import com.example.ourchat.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j

public class SocketIOConfig {

    @Autowired
    private JwtProperties jwtProperties;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname("0.0.0.0");
        config.setPort(9092);
        config.setOrigin("*");
        config.setPingInterval(60000);
        config.setPingTimeout(25000); // 注意：这里是 setPingTimeout
        SocketIOServer server = new SocketIOServer(config);

        // 添加连接监听器
        server.addConnectListener(client -> {
            // 从 URL 参数中获取 token
            String token = client.getHandshakeData().getSingleUrlParam("token");

            if (token == null) {
                log.warn("未提供 token，断开连接");
                client.disconnect();
                return;
            }

            // 验证 token
            if (!isValidToken(token, client)) {
                log.warn("无效的 token，断开连接: {}", token);
                client.disconnect();
            } else {
                Long userId = getUserIdFromToken(token);
                log.info("用户{}连接成功", userId);
                // 可选：保存用户会话，例如 client.set("userId", userId);
            }
        });

        return server;
    }
    /**
     * 验证 token 是否有效
     */
    private boolean isValidToken(String token, SocketIOClient client) {
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Date exp = claims.getExpiration();
            if (exp.before(new Date())) {
                log.warn("Token 已过期");

                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("Token 验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从 token 中提取用户 ID
     */
    private Long getUserIdFromToken(String token) {
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            return Long.valueOf(claims.get(JWtClaimsConstant.USER_ID).toString());
        } catch (Exception e) {
            log.error("从 token 获取用户 ID 失败: {}", e.getMessage());
            return null; // 理论上不会到达这里，因为 isValidToken 已验证过
        }
    }

}
