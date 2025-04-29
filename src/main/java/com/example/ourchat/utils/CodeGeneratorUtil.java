package com.example.ourchat.utils;

import java.util.UUID;

/**
 * 生成验证码
 */
public class CodeGeneratorUtil {
    /**
     * 生成指定长度的验证码
     * @param length
     * @return
     */
    public static String generateCode(int length) {
        return UUID.randomUUID().toString().substring(0, length);
    }
}
