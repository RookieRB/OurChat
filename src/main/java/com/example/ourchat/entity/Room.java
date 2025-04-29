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
public class Room {
    private Long roomId;
    private String roomName;
    private String description;
    private Date createdAt;
    private String imgUrl;
}
