package com.interno.assist.exceptionHandling;

public enum ErrorCodes {
    INTERNAL_SERVER_ERROR("500", "Internal Server Error"),
    ILLEGAL_ARGUMENT_ERROR("400", "Illegal Argument Error");

    private final String code;
    private final String status;

    ErrorCodes(String code, String status) {
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }
}
