package com.example.ourchat.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ourchat.jwt")
@Data
public class JwtProperties {
    /**
     *
     * 用户生成jwt令牌相关配置
     * */
    private String userSecretKey;
    private long userTtl;
    private String userTokenName;
}
