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

public class FriendRequest {

    private Long requestId;
    private Long senderId;
    private Long receiverId;
    private String requestMessage;
    private Integer status; // 0:待处理, 1:已接受, 2:已拒绝
    private Date createdAt;
    private Date updatedAt;
}
