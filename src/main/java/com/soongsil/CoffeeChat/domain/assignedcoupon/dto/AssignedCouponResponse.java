package com.soongsil.CoffeeChat.domain.assignedcoupon.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AssignedCouponResponse {

    private String couponNumber;
    private String name;
    private String status;
    private LocalDateTime issuedAt;
    private LocalDateTime usedAt;
}