package com.soongsil.CoffeeChat.domain.dto;

import com.soongsil.CoffeeChat.domain.entity.enums.PartEnum;

import lombok.*;

public class MenteeResponse {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MenteeInfoResponse {
        private PartEnum part;
    }
}
