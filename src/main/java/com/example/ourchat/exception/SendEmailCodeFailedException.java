package com.example.ourchat.exception;

public class SendEmailCodeFailedException extends BaseException{

    public SendEmailCodeFailedException() {
    }
    public SendEmailCodeFailedException(String msg) {
        super(msg);
    }
}
