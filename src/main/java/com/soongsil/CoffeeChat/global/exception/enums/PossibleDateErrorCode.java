package com.soongsil.CoffeeChat.global.exception.enums;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PossibleDateErrorCode {
    // 400
    POSSIBLE_DATE_NOT_FOUND(HttpStatus.NOT_FOUND, "POSSIBLE_DATE_404", "가능시간을 찾을 수 없습니다."),
    PREEMPTED_POSSIBLE_DATE(HttpStatus.GONE, "POSSIBLE_DATE_410", "선점된 가능시간입니다.");

    private final HttpStatusCode httpStatusCode;
    private final String errorCode;
    private final String errorMessage;
}
