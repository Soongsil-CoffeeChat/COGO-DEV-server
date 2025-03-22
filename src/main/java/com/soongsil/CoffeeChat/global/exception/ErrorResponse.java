package com.soongsil.CoffeeChat.global.exception;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    private final int httpStatus;
    private final String error;
    private final String message;

    public ErrorResponse(int httpStatus, String error, String message) {
        this(LocalDateTime.now(), httpStatus, error, message);
    }

    public ErrorResponse(GlobalErrorCode globalErrorCode) {
        this(
                LocalDateTime.now(),
                globalErrorCode.getHttpStatus().value(),
                globalErrorCode.getErrorCode(),
                globalErrorCode.getMessage());
    }

    public ErrorResponse(GlobalErrorCode globalErrorCode, String message) {
        this(
                LocalDateTime.now(),
                globalErrorCode.getHttpStatus().value(),
                globalErrorCode.getErrorCode(),
                message != null ? message : globalErrorCode.getMessage());
    }
}
