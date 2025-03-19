package com.soongsil.CoffeeChat.dto;

import com.soongsil.CoffeeChat.enums.PartEnum;

import lombok.*;

public class MenteeResponse {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MenteeInfoResponse {
        private PartEnum part;
        private boolean isNewAccount;
    }
}
