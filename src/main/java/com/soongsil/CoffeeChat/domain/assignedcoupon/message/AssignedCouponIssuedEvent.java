package com.soongsil.CoffeeChat.domain.assignedcoupon.message;

import java.time.LocalDateTime;

// 지정 쿠폰 비동기 로깅용 dto
public record AssignedCouponIssuedEvent(
        String username,
        String name,
        String phoneNum,
        String couponNumber,
        LocalDateTime issuedAt) {
}