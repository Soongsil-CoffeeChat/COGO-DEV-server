package com.soongsil.CoffeeChat.controller.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404", "해당 USER의 엔티티가 존재하지 않습니다."),
    USER_SMS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "USER_500", "SMS 전송에 실패했습니다.");

    private final HttpStatusCode httpStatusCode;
    private final String errorCode;
    private final String errorMessage;
}
