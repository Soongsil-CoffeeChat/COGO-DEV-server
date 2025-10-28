package com.soongsil.CoffeeChat.domain.application.dto;

import com.soongsil.CoffeeChat.domain.application.enums.ApplicationRejectReason;
import com.soongsil.CoffeeChat.domain.application.enums.ApplicationStatus;

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
    public static class ApplicationStatusUpdateRequest {
        private ApplicationStatus status;
        private ApplicationRejectReason reason;
    }
}
