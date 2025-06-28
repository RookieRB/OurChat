package com.example.ourchat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor


public class AddFriendRequestDTO {
    private Long senderId;
    private Long receiverId;
    private String requestMessage;
}
