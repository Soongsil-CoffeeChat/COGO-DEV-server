package com.soongsil.CoffeeChat.domain.assignedcoupon.dto;

import java.util.List;

public record AssignedCouponRegisterResult(
        int totalRequested,
        int newlyRegistered,
        int duplicated,
        List<String> failedPhoneNums) {
}