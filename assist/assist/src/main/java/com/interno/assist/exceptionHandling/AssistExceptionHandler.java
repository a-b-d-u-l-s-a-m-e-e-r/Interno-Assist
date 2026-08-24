package com.interno.assist.exceptionHandling;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AssistExceptionHandler {

    @ExceptionHandler(ApplicationRuntimeException.class)
    public ErrorResponse handleApplicationRuntimeException(ApplicationRuntimeException ex) {
        return new ErrorResponse(ex.getMessage(), ErrorCodes.INTERNAL_SERVER_ERROR.getStatus(), ErrorCodes.INTERNAL_SERVER_ERROR.getCode(), java.time.LocalDateTime.now());
        }

    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleGenericException(IllegalArgumentException ex) {
        return new ErrorResponse(ex.getMessage(), ErrorCodes.ILLEGAL_ARGUMENT_ERROR.getStatus(), ErrorCodes.ILLEGAL_ARGUMENT_ERROR.getCode(), java.time.LocalDateTime.now());
    }
}
