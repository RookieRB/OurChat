package com.example.ourchat.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomVO {
    private Long roomId;
    private String roomName;
    private String description;
    private String imgUrl;
    private Integer onlineNumber;

}
