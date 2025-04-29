package com.example.ourchat.exception;

public class LoginInfoExpireException extends BaseException{
    public LoginInfoExpireException() {
    }

    public LoginInfoExpireException(String msg){
        super(msg);
    }
}
