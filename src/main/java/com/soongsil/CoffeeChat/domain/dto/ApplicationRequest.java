package com.soongsil.CoffeeChat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ApplicationRequest {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplicationCreateRequest {
        private Long mentorId;
        private Long possibleDateId;
        private String memo;
    }
}
