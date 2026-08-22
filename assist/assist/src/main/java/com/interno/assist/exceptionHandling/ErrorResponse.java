package com.interno.assist.exceptionHandling;

import lombok.Data;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String message;
    private String status;
    private String code;
    private LocalDateTime timeStamp;

    public ErrorResponse(String message, String status, String code, LocalDateTime timeStamp) {
        this.message = message;
        this.status = status;
        this.code = code;
        this.timeStamp = timeStamp;
    }

}
