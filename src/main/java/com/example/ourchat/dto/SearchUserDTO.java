package com.example.ourchat.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchUserDTO {
    private String keyword;     // 搜索关键词
    private Integer limit;     // 限制返回结果数量，可选
    private Long currentUserId; // 当前用户ID，用于排除自己
}
