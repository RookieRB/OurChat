package com.example.ourchat.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResult {
    private Integer code;   // 错误状态码
    private String message; // 错误信息
}
