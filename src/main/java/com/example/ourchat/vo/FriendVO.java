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
public class FriendVO {
    private Long userId;
    private String nickname;
    private String imgUrl;
    private Date lastLogin;
    private String email;
    private String phone;
    private int isActive;
}
