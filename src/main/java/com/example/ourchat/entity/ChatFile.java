package com.example.ourchat.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_files")
public class ChatFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_id", nullable = false, length = 32)
    private String fileId;

    @Column(name = "chat_id", nullable = true)
    private Long chatId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "storage_type", nullable = false)
    private Integer storageType; // 1-数据库 2-文件系统

    @Column(name = "file_path")
    private String filePath;

    @Lob
    @Column(name = "file_content")
    private byte[] fileContent;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "status", nullable = false)
    private Integer status = 1;                 // 文件状态管理 1:正常/可用 0:已删除/不可用

    @Column(name = "chat_type", nullable = false)
    private Integer chatType = 1;  // 1- 用户之间的聊天 2-聊天室聊天
}
