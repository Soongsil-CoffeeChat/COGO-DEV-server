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
        private Long id;
        private String name;
        private String lastChat;
        private LocalDateTime updatedAt;
        private ChatApplicationResponse application;
        private List<ChatParticipantResponse> participants;
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
        private Long id;
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
        private Long id;
        private String name;
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
        private String profileImage;
    }

    /** 채팅 코고 정보 응답 DTO */
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ChatApplicationResponse {
        private Long applicationId;
    }
}
