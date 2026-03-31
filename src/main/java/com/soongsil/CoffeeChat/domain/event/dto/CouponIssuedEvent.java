package com.soongsil.CoffeeChat.domain.event.dto;

import java.time.LocalDateTime;

// 이벤트 비동기 로깅용 dto
public record CouponIssuedEvent(
        Long applicationId,
        Long menteeId,
        Long mentorId,
        String couponUrl,
        LocalDateTime issuedAt) {}
