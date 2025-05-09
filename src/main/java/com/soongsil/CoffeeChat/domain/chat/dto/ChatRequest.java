package com.soongsil.CoffeeChat.domain.chat.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

public class ChatRequest {

    /** 채팅방 생성 요청 DTO */
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateChatRoomRequest {
        private String name;
        private Long participantId;
    }

    /** 채팅 메시지 전송 요청 DTO */
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SendMessageRequest {
        private Long roomId;
        private String message;
        private MultipartFile image;
    }
}
