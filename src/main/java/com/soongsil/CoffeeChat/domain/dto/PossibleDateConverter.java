package com.soongsil.CoffeeChat.domain.dto;

import com.soongsil.CoffeeChat.domain.dto.PossibleDateRequest.*;
import com.soongsil.CoffeeChat.domain.entity.PossibleDate;

public class PossibleDateConverter {
    public static PossibleDate toEntity(PossibleDateCreateRequest request) {
        return PossibleDate.builder()
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isActive(true)
                .build();
    }

    public static PossibleDateResponse.PossibleDateCreateResponse toResponse(
            PossibleDate possibleDate) {
        return PossibleDateResponse.PossibleDateCreateResponse.builder()
                .date(possibleDate.getDate())
                .startTime(possibleDate.getStartTime())
                .endTime(possibleDate.getEndTime())
                .possibleDateId(possibleDate.getId())
                .isActive(possibleDate.isActive())
                .build();
    }
}
