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
public class ChatFileMessage {

    public Long id;
    public Long messageId;
    public String fileId;
    public Date createdAt;

}
