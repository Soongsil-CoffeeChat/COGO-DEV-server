package com.soongsil.CoffeeChat.dto;

import com.soongsil.CoffeeChat.dto.PossibleDateRequest.*;
import com.soongsil.CoffeeChat.dto.PossibleDateResponse.*;
import com.soongsil.CoffeeChat.entity.PossibleDate;

public class PossibleDateConverter {
    public static PossibleDate toEntity(PossibleDateCreateRequest request) {
        return PossibleDate.builder()
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isActive(true)
                .build();
    }

    public static PossibleDateCreateResponse toResponse(PossibleDate possibleDate) {
        return PossibleDateCreateResponse.builder()
                .date(possibleDate.getDate())
                .startTime(possibleDate.getStartTime())
                .endTime(possibleDate.getEndTime())
                .possibleDateId(possibleDate.getId())
                .isActive(possibleDate.isActive())
                .build();
    }
}
