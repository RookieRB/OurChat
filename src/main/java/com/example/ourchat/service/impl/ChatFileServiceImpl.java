package com.example.ourchat.service.impl;

import com.example.ourchat.entity.ChatFile;
import com.example.ourchat.service.ChatFileService;
import com.example.ourchat.repository.ChatFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


@Service
@Slf4j
public class ChatFileServiceImpl implements ChatFileService {
    @Value("${ourchat.file.upload.path}")
    private String uploadPath;

    @Value("${ourchat.file.size.threshold:1048576}")// 1MB
    private long fileSizeThreshold;

    @Autowired
    private ChatFileRepository chatFileRepository;

    @Override
    public ChatFile uploadFile(MultipartFile file, Long senderId, Long receiverId,int chatType) throws IOException {
        String fileId = UUID.randomUUID().toString().replace("-", "");
        ChatFile chatFile = new ChatFile();
        chatFile.setFileId(fileId);
        chatFile.setSenderId(senderId);
        chatFile.setReceiverId(receiverId);
        chatFile.setOriginalName(file.getOriginalFilename());
        chatFile.setFileSize(file.getSize());
        chatFile.setFileType(file.getContentType());
        chatFile.setCreatedAt(LocalDateTime.now());
        chatFile.setUpdatedAt(LocalDateTime.now()); // 添加这一行
        chatFile.setChatType(chatType);
        chatFile.setStatus(1);


        // 根据文件大小决定存储方式
        if (file.getSize() < fileSizeThreshold) {
            // 小文件存储在数据库
            chatFile.setStorageType(1);
            chatFile.setFileContent(file.getBytes());
        } else {
            // 大文件存储在文件系统
            chatFile.setStorageType(2);
            String filePath = saveToFileSystem(file, fileId);
            chatFile.setFilePath(filePath);
        }

        return chatFileRepository.save(chatFile);
    }

    private String saveToFileSystem(MultipartFile file, String fileId) throws IOException {
        // 创建年月日目录
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Path uploadDir = Paths.get(uploadPath, datePath);
        Files.createDirectories(uploadDir);

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 构建文件路径
        String fileName = fileId + extension;
        Path targetLocation = uploadDir.resolve(fileName);

        // 保存文件
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return datePath + "/" + fileName;
    }

    @Override
    public byte[] downloadFile(String fileId) throws IOException {
        ChatFile chatFile = chatFileRepository.findByFileId(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found: " + fileId));

        if (chatFile.getStorageType() == 1) {
            // 从数据库获取
            return chatFile.getFileContent();
        } else {
            // 从文件系统获取
            Path filePath = Paths.get(uploadPath, chatFile.getFilePath());
            return Files.readAllBytes(filePath);
        }
    }

    @Override
    public void deleteFile(String fileId) throws IOException {
        ChatFile chatFile = chatFileRepository.findByFileId(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found: " + fileId));

        if (chatFile.getStorageType() == 2) {
            // 删除文件系统中的文件
            Path filePath = Paths.get(uploadPath, chatFile.getFilePath());
            Files.deleteIfExists(filePath);
        }

        // 软删除
        chatFile.setStatus(0);
        chatFileRepository.save(chatFile);
    }
}
