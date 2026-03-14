package com.soongsil.CoffeeChat.domain.chat.dto;

import org.jetbrains.annotations.NotNull;

import lombok.*;

public class ChatRequest {

    /** 채팅방 생성 요청 DTO */
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateChatRoomRequest {
        @NotNull private Long applicationId;
        @NotNull private Long participantUserId;
    }

    /** 채팅 메시지 전송 요청 DTO */
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SendMessageRequest {
        private Long roomId;
        private String message;
    }
}
