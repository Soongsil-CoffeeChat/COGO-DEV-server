package com.soongsil.CoffeeChat.domain.assignedcoupon.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignedCouponTargetRequest(
        @NotBlank String name,
        @NotBlank String phoneNum) {
}