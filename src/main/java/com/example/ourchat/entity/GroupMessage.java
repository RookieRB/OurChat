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
public class GroupMessage {
    private Long messageId;
    private Long roomId;
    private Long userId;
    private String content;
    private Date sentAt;
}
