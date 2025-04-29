package com.example.ourchat.handler;

import com.example.ourchat.exception.AccountNotFoundException;
import com.example.ourchat.exception.LoginInfoExpireException;
import com.example.ourchat.exception.PasswordErrorException;
import com.example.ourchat.result.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // 处理用户不存在的异常
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResult> handleAccountNotFoundException(AccountNotFoundException e) {
        ErrorResult error = new ErrorResult(0, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 处理密码错误的异常
    @ExceptionHandler(PasswordErrorException.class)
    public ResponseEntity<ErrorResult> handlePasswordErrorException(PasswordErrorException e) {
        ErrorResult error = new ErrorResult(0, e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // 处理 Token 失效异常
    @ExceptionHandler(LoginInfoExpireException.class)
    public ResponseEntity<ErrorResult> handleLoginInfoExpire(LoginInfoExpireException e) {
        ErrorResult error = new ErrorResult(1234, e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    //

}
