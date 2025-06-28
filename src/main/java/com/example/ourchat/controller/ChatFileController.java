package com.example.ourchat.controller;

import com.example.ourchat.entity.ChatFile;
import com.example.ourchat.service.ChatFileService;
import com.example.ourchat.repository.ChatFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType; // 添加这行导入 Spring 的 MediaType
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping("/chat/files")
@Slf4j
public class ChatFileController {
    @Autowired
    private ChatFileService chatFileService;

    @Autowired
    private ChatFileRepository chatFileRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("senderId") Long senderId,
            @RequestParam("receiverId") Long receiverId,
            @RequestParam("chatType") int chatType
            ) {
        try {
            ChatFile chatFile = chatFileService.uploadFile(file, senderId, receiverId,chatType);

            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("code", 1);
                put("message", "File uploaded successfully");
                put("data", chatFile);
            }});
        } catch (IOException e) {
            log.error("File upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HashMap<String, Object>() {{
                        put("code", 0);
                        put("message", "File upload failed");
                    }});
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileId) {
        try {
            byte[] fileData = chatFileService.downloadFile(fileId);
            ChatFile chatFile = chatFileRepository.findByFileId(fileId)
                    .orElseThrow(() -> new FileNotFoundException("File not found: " + fileId));

            ByteArrayResource resource = new ByteArrayResource(fileData);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(chatFile.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + chatFile.getOriginalName() + "\"")
                    .body(resource);
        } catch (IOException e) {
            log.error("文件下载失败", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HashMap<String, Object>() {{
                        put("code", 0);
                        put("message", "文件不存在或下载失败");
                    }});
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileId) {
        try {
            chatFileService.deleteFile(fileId);
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("code", 1);
                put("message", "文件删除成功");
            }});
        } catch (Exception e) {
            log.error("文件删除失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HashMap<String, Object>() {{
                        put("code", 0);
                        put("message", "文件删除失败: " + e.getMessage());
                    }});
        }
    }

}
