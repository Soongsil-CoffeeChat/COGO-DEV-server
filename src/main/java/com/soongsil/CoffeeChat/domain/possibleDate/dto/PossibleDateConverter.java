package com.soongsil.CoffeeChat.domain.possibleDate.dto;

import com.soongsil.CoffeeChat.domain.mentor.entity.Mentor;
import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateRequest.*;
import com.soongsil.CoffeeChat.domain.possibleDate.entity.PossibleDate;

public class PossibleDateConverter {
    public static PossibleDate toEntity(PossibleDateCreateRequest request, Mentor mentor) {
        PossibleDate possibleDate =
                PossibleDate.builder()
                        .date(request.getDate())
                        .startTime(request.getStartTime())
                        .endTime(request.getEndTime())
                        .isActive(true)
                        .mentor(mentor)
                        .build();
        mentor.addPossibleDate(possibleDate);
        return possibleDate;
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
