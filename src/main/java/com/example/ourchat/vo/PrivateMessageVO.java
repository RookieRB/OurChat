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
public class PrivateMessageVO {
    private Long messageId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Date sentAt;
    private Boolean hasFile;
    private String[] originalNameList;
    private String[] fileIdList;
    private String[] fileType;
    private Long[] fileSize;
    private String type;
}
