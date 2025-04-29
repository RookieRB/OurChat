package com.example.ourchat.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginVO {
    // 用户id
    private Long userId;
    // 用户名
    private String username;
    // 昵称
    private String nickname;
    // 邮箱
    private String email;
    // 手机号
    private String phone;
    // 用户头像
    private String imgUrl;
    // jwt令牌
    private String token;
    // 最后一次登录
    private Date lastLogin;
    // 当前用户状态
    private int isActive;
}
