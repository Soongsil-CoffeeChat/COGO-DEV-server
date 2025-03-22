package com.soongsil.CoffeeChat.global.exception.enums;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ApplicationErrorCode {
    // 400
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "APPLICATION_404", "COGO를 찾을 수 없습니다."),
    INVALID_MATCH_STATUS(HttpStatus.BAD_REQUEST, "APPLICATION_400", "매칭 수락 여부를 잘못 입력하였습니다.");

    private final HttpStatusCode httpStatusCode;
    private final String errorCode;
    private final String errorMessage;
}
