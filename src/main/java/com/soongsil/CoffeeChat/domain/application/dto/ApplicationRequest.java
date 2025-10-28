package com.soongsil.CoffeeChat.domain.application.dto;

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

    @Getter
    // 왜 여기도 NoA~? Builder 랑 다른가
    public static class ApplicationStatusUpdateRequest {
        private String decision;
        private String reason;
    }
}
