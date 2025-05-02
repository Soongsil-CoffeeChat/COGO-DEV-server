package com.soongsil.CoffeeChat.domain.mentee.dto;

import com.soongsil.CoffeeChat.domain.mentor.enums.PartEnum;

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
