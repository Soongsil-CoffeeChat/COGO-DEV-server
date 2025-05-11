package com.soongsil.CoffeeChat.domain.fcmNotification.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.Builder;

@Builder(access = PRIVATE)
public record FcmMessageRequest(String token, String title, String body) {
    public static FcmMessageRequest of(String token, String title, String body) {
        return FcmMessageRequest.builder().token(token).title(title).body(body).build();
    }
}
