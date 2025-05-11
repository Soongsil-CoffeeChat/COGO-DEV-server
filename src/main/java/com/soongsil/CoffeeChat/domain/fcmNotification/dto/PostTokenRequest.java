package com.soongsil.CoffeeChat.domain.fcmNotification.dto;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class PostTokenRequest {

    @NotBlank(message = "토큰을 입력해야 합니다.")
    @Schema(description = "FCM 등록 토큰", example = "dddsd")
    private String token;

    public PostTokenRequest() {}

    public PostTokenRequest(String token) {
        this.token = token;
    }
}
