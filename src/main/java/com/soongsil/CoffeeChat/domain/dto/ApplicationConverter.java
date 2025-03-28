package com.soongsil.CoffeeChat.domain.dto;

import com.soongsil.CoffeeChat.domain.entity.Application;
import com.soongsil.CoffeeChat.domain.entity.Mentee;
import com.soongsil.CoffeeChat.domain.entity.Mentor;
import com.soongsil.CoffeeChat.domain.entity.PossibleDate;

public class ApplicationConverter {
    public static Application toEntity(
            Mentor mentor, Mentee mentee, String memo, PossibleDate possibleDate) {
        return Application.builder()
                .mentor(mentor)
                .mentee(mentee)
                .memo(memo)
                .possibleDate(possibleDate)
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
            Application application, String mentorName, String menteeName) {
        return ApplicationResponse.ApplicationGetResponse.builder()
                .applicationId(application.getId())
                .mentorName(mentorName)
                .menteeName(menteeName)
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
}
