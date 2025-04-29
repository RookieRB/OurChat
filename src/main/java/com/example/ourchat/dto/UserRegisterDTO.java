package com.example.ourchat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {
    // 用户名
    private String username;
    // 用户密码
    private String password;
    // 邮箱
    private String email;
    // 验证码
    private String confirmCode;
}
