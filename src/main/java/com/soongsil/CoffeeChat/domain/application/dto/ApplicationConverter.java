package com.soongsil.CoffeeChat.domain.application.dto;

import com.soongsil.CoffeeChat.domain.application.entity.Application;
import com.soongsil.CoffeeChat.domain.mentee.entity.Mentee;
import com.soongsil.CoffeeChat.domain.mentor.entity.Mentor;
import com.soongsil.CoffeeChat.domain.possibleDate.entity.PossibleDate;

public class ApplicationConverter {
    public static Application toEntity(
            Mentor mentor, Mentee mentee, String memo, PossibleDate possibleDate) {
        return Application.builder()
                .mentor(mentor)
                .mentee(mentee)
                .memo(memo)
                .possibleDate(possibleDate)
                .rejectReason(null)
                .build();
    }

    public static ApplicationResponse.ApplicationCreateResponse toResponse(
            Application application) {
        return ApplicationResponse.ApplicationCreateResponse.builder()
                .applicationId(application.getId())
                .mentorId(application.getMentor().getId())
                .menteeId(application.getMentee().getId())
                .applicationMemo(application.getMemo())
                .applicationDate(application.getPossibleDate().getDate())
                .applicationStartTime(application.getPossibleDate().getStartTime())
                .applicationEndTime(application.getPossibleDate().getEndTime())
                .build();
    }

    public static ApplicationResponse.ApplicationGetResponse toGetResponse(
            Application application, Long mentorId, Long menteeId) {
        return ApplicationResponse.ApplicationGetResponse.builder()
                .applicationId(application.getId())
                .mentorId(mentorId)
                .menteeId(menteeId)
                .applicationMemo(application.getMemo())
                .applicationDate(application.getPossibleDate().getDate())
                .applicationStartTime(application.getPossibleDate().getStartTime())
                .applicationEndTime(application.getPossibleDate().getEndTime())
                .build();
    }

    public static ApplicationResponse.ApplicationMatchResponse toResponse(
            Long applicationId, String status) {
        return ApplicationResponse.ApplicationMatchResponse.builder()
                .applicationId(applicationId)
                .applicationStatus(status)
                .build();
    }

    public static ApplicationSummaryResponse toSummaryResponse(
            Application application, String otherPartyUserName) {
        return ApplicationSummaryResponse.builder()
                .applicationId(application.getId())
                .otherPartyName(otherPartyUserName)
                .applicationDate(application.getPossibleDate().getDate())
                .build();
    }
}
