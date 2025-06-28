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
public class UserAddVO {
    private Long userId;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private int isActive;
    private String imgUrl;
    private Date lastLogin;
    private boolean isAlreadyFriend;
}
