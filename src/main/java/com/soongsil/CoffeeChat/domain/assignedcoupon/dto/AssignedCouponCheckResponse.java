package com.soongsil.CoffeeChat.domain.assignedcoupon.dto;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class AssignedCouponCheckResponse {

    private boolean eligible;
    private boolean alreadyIssued;
    private String name;
    private String couponNumber;
    private String status;
    private LocalDateTime issuedAt;
    private LocalDateTime usedAt;

    public static AssignedCouponCheckResponse notEligible() {
        return AssignedCouponCheckResponse.builder()
                .eligible(false)
                .alreadyIssued(false)
                .build();
    }
}
