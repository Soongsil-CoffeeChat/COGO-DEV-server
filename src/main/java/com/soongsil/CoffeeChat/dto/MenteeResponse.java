package com.soongsil.CoffeeChat.dto;

import com.soongsil.CoffeeChat.entity.enums.PartEnum;

import lombok.*;

public class MenteeResponse {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MenteeInfoResponse {
        private PartEnum part;
        private boolean isNewAccount;
    }
}
