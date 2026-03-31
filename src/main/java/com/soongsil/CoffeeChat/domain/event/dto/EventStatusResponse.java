package com.soongsil.CoffeeChat.domain.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventStatusResponse {
    private String status;
    private Long remainingCount;
}
