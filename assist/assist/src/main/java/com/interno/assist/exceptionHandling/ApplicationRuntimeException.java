package com.interno.assist.exceptionHandling;

public class ApplicationRuntimeException extends RuntimeException{

    public ApplicationRuntimeException(String message) {
        super(message);
    }
}
