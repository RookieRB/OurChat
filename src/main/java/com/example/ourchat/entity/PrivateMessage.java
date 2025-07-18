package com.example.ourchat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrivateMessage {
    private Long messageId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private boolean hasFile; // 表示此消息是否包含文件
    private Date sentAt;
    private String type;
}
