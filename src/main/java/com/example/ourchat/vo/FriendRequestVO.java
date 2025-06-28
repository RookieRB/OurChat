package com.example.ourchat.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class FriendRequestVO {

    private Long requestId;
    private Long senderId;
    private Long receiverId;
    private String requestMessage;
    private Integer status; // 0:待处理, 1:已接受, 2:已拒绝
    private Date createdAt;
    private Date updatedAt;

    // 非数据库字段，用于前端展示
    private String senderName;
    private String senderAvatar;
}
