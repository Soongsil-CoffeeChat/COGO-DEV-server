package com.soongsil.CoffeeChat.dto;

import com.soongsil.CoffeeChat.enums.PartEnum;

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
