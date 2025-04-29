package com.example.ourchat.result;
import lombok.Data;

import java.io.Serializable;
@Data
public class Result<T> implements Serializable {

    private Integer code; // 编码信息，1代表返回成功，0和其他数字代表失败
    private String msg; // 错误信息描述
    private T data; // 返回的数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<>(); // 创建一个Result对象
        result.code = 1;
        return result;
    }

    public static <T> Result<T> success(T object){
        Result<T> result = new Result<>();
        result.code = 1;
        result.data = object;
        return result;
    }

    public static <T> Result<T> error(String msg){
        Result<T> result = new Result<>();
        result.code = 0;
        result.msg = msg;
        return result;
    }
}
