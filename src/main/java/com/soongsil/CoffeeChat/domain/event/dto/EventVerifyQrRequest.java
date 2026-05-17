package com.soongsil.CoffeeChat.domain.event.dto;

import jakarta.validation.constraints.NotBlank;

public record EventVerifyQrRequest(
        @NotBlank String qrToken
) {
}
