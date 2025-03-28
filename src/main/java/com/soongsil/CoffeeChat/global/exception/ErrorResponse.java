package com.soongsil.CoffeeChat.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final int httpStatus;
    private final String error;
    private final String message;

    public ErrorResponse(GlobalErrorCode globalErrorCode) {
        this(
                globalErrorCode.getHttpStatus().value(),
                globalErrorCode.getErrorCode(),
                globalErrorCode.getMessage());
    }

    public ErrorResponse(GlobalErrorCode globalErrorCode, String message) {
        this(
                globalErrorCode.getHttpStatus().value(),
                globalErrorCode.getErrorCode(),
                message != null ? message : globalErrorCode.getMessage());
    }
}
