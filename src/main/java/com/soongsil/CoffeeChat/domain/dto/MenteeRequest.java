package com.soongsil.CoffeeChat.domain.dto;

import com.soongsil.CoffeeChat.domain.entity.enums.PartEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MenteeRequest {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenteeJoinRequest {
        private PartEnum part;
    }
}
