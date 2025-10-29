package com.soongsil.CoffeeChat.domain.possibleDate.dto;

import com.soongsil.CoffeeChat.domain.mentor.entity.Mentor;
import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateRequest.PossibleDateCreateUpdateRequest;
import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateResponse.PossibleDateCreateUpdateResponse;
import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateResponse.PossibleDateDetailResponse;
import com.soongsil.CoffeeChat.domain.possibleDate.entity.PossibleDate;

public class PossibleDateConverter {
    public static PossibleDate toEntity(PossibleDateCreateUpdateRequest request, Mentor mentor) {
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

    public static PossibleDateDetailResponse toResponse(PossibleDate possibleDate) {
        return PossibleDateDetailResponse.builder()
                .date(possibleDate.getDate())
                .startTime(possibleDate.getStartTime())
                .endTime(possibleDate.getEndTime())
                .possibleDateId(possibleDate.getId())
                .isActive(possibleDate.isActive())
                .build();
    }

    public static PossibleDateCreateUpdateResponse toCreateUpdateResponse(
            PossibleDate possibleDate) {
        return PossibleDateCreateUpdateResponse.builder()
                .date(possibleDate.getDate())
                .startTime(possibleDate.getStartTime())
                .endTime(possibleDate.getEndTime())
                .possibleDateId(possibleDate.getId())
                .isActive(possibleDate.isActive())
                .build();
    }

    public static void updateEntity(
            PossibleDate possibleDate, PossibleDateCreateUpdateRequest request) {
        possibleDate.updatePossibleDateTime(
                request.getDate(), request.getStartTime(), request.getEndTime());
    }
}
