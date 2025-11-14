package com.soongsil.CoffeeChat.domain.chat.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

public class ChatResponse {

    /** 채팅방 목록 페이징 응답 DTO */
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ChatRoomPageResponse {
        private List<ChatRoomResponse> content;
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean last;
    }

    /** 채팅방 응답 DTO */
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ChatRoomResponse {
        private Long roomId;
        private String lastChat;
        private LocalDateTime updatedAt;

        private String otherPartyName;
        private String otherPartyProfileImage;

        // 사용자 받가 or 필요한 부분만 받기
        /*
        1: User 받기
        2: User - ChatRoomUser 끌고와서
         */
    }

    /** 채팅 메시지 페이징 응답 DTO */
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ChatMessagePageResponse {
        private List<ChatMessageResponse> content;
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean last;
    }

    /** 채팅 메시지 응답 DTO */
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ChatMessageResponse {
        private Long chatId;
        private Long senderId;
        private String message;
        private LocalDateTime createdAt;
    }

    /** 채팅방 상세 정보 응답 DTO */
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ChatRoomDetailResponse {
        private Long roomId;
        private List<ChatParticipantResponse> participants;
    }

    /** 채팅 참여자 정보 응답 DTO */
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ChatParticipantResponse {
        private Long userId;
        private String username;
        private String name;
        private String profileImage;
    }
}
