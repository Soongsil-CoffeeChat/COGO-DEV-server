package com.soongsil.CoffeeChat.global.exception.enums;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MentorErrorCode {
    // 400
    MENTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "MENTOR_404", "멘토를 찾을 수 없습니다.");

    private final HttpStatusCode httpStatusCode;
    private final String errorCode;
    private final String errorMessage;
}
