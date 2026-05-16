package com.soongsil.CoffeeChat.domain.assignedcoupon.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignedCouponIssueRequest(
        @NotBlank String storePin) {
}
