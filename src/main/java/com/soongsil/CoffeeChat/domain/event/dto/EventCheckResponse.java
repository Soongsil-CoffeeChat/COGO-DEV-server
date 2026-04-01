package com.soongsil.CoffeeChat.domain.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EventCheckResponse {
    private boolean isAlreadyIssued;
    private boolean isLimitExceeded;
    private boolean canIssue;
}
