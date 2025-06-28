package com.example.ourchat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendUser {
    Long userId;
    String nickname;
    int sameFriendsNumber;
    String imgUrl;
}
