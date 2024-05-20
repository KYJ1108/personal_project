package com.example.personal_project.user;

public class CustomException extends Throwable{
    private ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
