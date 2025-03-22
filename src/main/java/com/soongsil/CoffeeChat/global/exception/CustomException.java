package com.soongsil.CoffeeChat.global.exception;

import org.springframework.http.HttpStatusCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {
    private final HttpStatusCode errorCode;
    private final String errorMessage;
}
