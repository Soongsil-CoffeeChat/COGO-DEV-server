package com.soongsil.CoffeeChat.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode {
    // 공통
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 에러, 관리자에게 문의 바립니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "요청 형식이 잘못되었습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_401", "인증 되지 않은 요청입니다."),

    // 유저 관련
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404", "해당 USER의 엔티티가 존재하지 않습니다."),
    USER_SMS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "USER_500", "SMS 전송에 실패했습니다."),
    USER_EXIST(HttpStatus.CONFLICT, "USER_409", "중복 유저가 존재합니다."),

    // 멘토 관련
    MENTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "MENTOR_404", "멘토를 찾을 수 없습니다."),

    // 커피쳇 관련
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "APPLICATION_404", "COGO를 찾을 수 없습니다."),
    APPLICATION_INVALID_MATCH_STATUS(
            HttpStatus.BAD_REQUEST, "APPLICATION_400", "매칭 수락 여부를 잘못 입력하였습니다."),

    // 커피쳇 가능 시간 관련
    POSSIBLE_DATE_NOT_FOUND(HttpStatus.NOT_FOUND, "POSSIBLE_DATE_404", "가능시간을 찾을 수 없습니다."),
    PREEMPTED_POSSIBLE_DATE(HttpStatus.GONE, "POSSIBLE_DATE_410", "선점된 가능시간입니다."),

    // 채팅 메시지 관련 오류
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MESSAGE_404", "메시지를 찾을 수 없습니다."),
    MESSAGE_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MESSAGE_500", "메시지 전송에 실패했습니다."),

    // 채팅방 관련 오류
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHATROOM_404", "채팅방을 찾을 수 없습니다."),
    CHATROOM_NOT_PARTICIPANT(HttpStatus.FORBIDDEN, "CHATROOM_403", "채팅방 참여자가 아닙니다."),

    // JWT 관련
    JWT_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "JWT_001", "유효하지 않는 토큰입니다."),
    JWT_MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, "JWT_002", "잘못된 형식의 토큰입니다."),
    JWT_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "JWT_003", "유효기간이 만료된 토큰입니다."),
    JWT_UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "JWT_004", "지원하지 않는 형식의 토큰입니다."),

    // Oauth 관련
    OAUTH_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OAUTH_500", "OAuth 서비스 측에서 에러가 발생했습니다."),

    // 어드민 관련
    ADMIN_INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, "ADMIN_400", "잘못된 비밀번호입니다"),
    ;

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;
}
