package com.soongsil.CoffeeChat.global.exception;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {
    private final GlobalErrorCode globalErrorCode;

    public GlobalException(GlobalErrorCode globalErrorCode) {
        super(globalErrorCode.getMessage());
        this.globalErrorCode = globalErrorCode;
    }

    public GlobalException(GlobalErrorCode globalErrorCode, Throwable cause) {
        super(globalErrorCode.getMessage(), cause);
        this.globalErrorCode = globalErrorCode;
    }
}
