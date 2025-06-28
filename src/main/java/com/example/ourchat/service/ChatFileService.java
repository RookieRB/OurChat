package com.example.ourchat.service;

import com.example.ourchat.entity.ChatFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ChatFileService {
    ChatFile uploadFile(MultipartFile file, Long senderId, Long receiverId,int chatType) throws IOException;
    byte[] downloadFile(String fileId) throws IOException;
    void deleteFile(String fileId) throws IOException;
}
