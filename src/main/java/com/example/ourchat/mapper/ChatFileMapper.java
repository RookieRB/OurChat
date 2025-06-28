package com.example.ourchat.mapper;

import com.example.ourchat.entity.ChatFile;
import com.example.ourchat.entity.ChatFileMessage;
import com.example.ourchat.entity.PrivateMessage;
import org.apache.ibatis.annotations.Mapper;

import java.io.File;

@Mapper
public interface ChatFileMapper {
    // 建立file与message之间的关系
    void insertChatFileMessage(ChatFileMessage chatFileMessage);

    // 通过messageID获得fileID;
    String[] getFileIdByMessageId(Long messageId);


    // 通过fileId查询file文件
    ChatFile getChatFileByFileId(String fileId);
}
