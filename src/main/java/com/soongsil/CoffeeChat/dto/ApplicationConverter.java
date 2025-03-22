package com.soongsil.CoffeeChat.dto;

import com.soongsil.CoffeeChat.dto.ApplicationResponse.ApplicationCreateResponse;
import com.soongsil.CoffeeChat.dto.ApplicationResponse.ApplicationGetResponse;
import com.soongsil.CoffeeChat.dto.ApplicationResponse.ApplicationMatchResponse;
import com.soongsil.CoffeeChat.entity.Application;
import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.PossibleDate;

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

    public static ApplicationCreateResponse toResponse(Application application) {
        return ApplicationCreateResponse.builder()
                .applicationId(application.getId())
                .mentorId(application.getMentor().getId())
                .menteeId(application.getMentee().getId())
                .applicationMemo(application.getMemo())
                .applicationDate(application.getPossibleDate().getDate())
                .applicationStartTime(application.getPossibleDate().getStartTime())
                .applicationEndTime(application.getPossibleDate().getEndTime())
                .build();
    }

    public static ApplicationGetResponse toGetResponse(
            Application application, String mentorName, String menteeName) {
        return ApplicationGetResponse.builder()
                .applicationId(application.getId())
                .mentorName(mentorName)
                .menteeName(menteeName)
                .applicationMemo(application.getMemo())
                .applicationDate(application.getPossibleDate().getDate())
                .applicationStartTime(application.getPossibleDate().getStartTime())
                .applicationEndTime(application.getPossibleDate().getEndTime())
                .build();
    }

    public static ApplicationMatchResponse toResponse(Long applicationId) {
        return ApplicationMatchResponse.builder()
                .applicationId(applicationId)
                .build();
    }
}
