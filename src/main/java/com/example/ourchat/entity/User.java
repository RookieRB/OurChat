package com.example.ourchat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long userId;
    private String username;
    private String nickname;
    private String password;
    private String email;
    private String phone;
    private int isActive;
    private String imgUrl;
    private Date lastLogin;
}
