package com.soongsil.CoffeeChat.controller.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
public enum RefreshErrorCode {
    // 400
    REFRESH_NOT_FOUND(HttpStatus.NOT_FOUND, "REFRESH_404", "refresh 토큰이 들어오지 않았습니다."),
    REFRESH_EXPIRED(HttpStatus.FORBIDDEN, "REFRESH_403", "refresh 토큰이 만료되었습니다."),
    REFRESH_NOT_MATCHED(HttpStatus.UNAUTHORIZED, "REFRESH_401", "저장되지 않은 refresh 토큰입니다."),
    REFRESH_BAD_REQUEST(HttpStatus.BAD_REQUEST, "REFRESH_400", "refresh 토큰이 아닌 다른 종류의 토큰이 들어왔습니다.");

    private final HttpStatusCode httpStatusCode;
    private final String errorCode;
    private final String errorMessage;
}
