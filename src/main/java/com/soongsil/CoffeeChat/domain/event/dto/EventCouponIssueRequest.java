package com.soongsil.CoffeeChat.domain.event.dto;

import jakarta.validation.constraints.NotBlank;

public record EventCouponIssueRequest (
        @NotBlank String qrToken,
        @NotBlank String storePin
){
}
