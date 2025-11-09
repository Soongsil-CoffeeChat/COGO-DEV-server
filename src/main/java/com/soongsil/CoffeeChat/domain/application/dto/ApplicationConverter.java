package com.soongsil.CoffeeChat.domain.application.dto;

import com.soongsil.CoffeeChat.domain.application.entity.Application;
import com.soongsil.CoffeeChat.domain.application.enums.ApplicationStatus;
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
                .rejectReason(null)
                .applicationStatus(ApplicationStatus.UNMATCHED)
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
                .rejectReason(application.getRejectReason())
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
                .applicationStatus(application.getApplicationStatus())
                .applicationRejectReason(application.getRejectReason())
                .applicationDate(application.getPossibleDate().getDate())
                .applicationStartTime(application.getPossibleDate().getStartTime())
                .applicationEndTime(application.getPossibleDate().getEndTime())
                .build();
    }

    public static ApplicationResponse.ApplicationUpdateResponse toUpdateResponse(
            Application application) {
        return ApplicationResponse.ApplicationUpdateResponse.builder()
                .applicationId(application.getId())
                .applicationStatus(application.getApplicationStatus())
                .reason(application.getRejectReason())
                .build();
    }

    public static ApplicationSummaryResponse toSummaryResponse(
            Application application, String otherPartyUserName) {
        return ApplicationSummaryResponse.builder()
                .applicationId(application.getId())
                .otherPartyName(otherPartyUserName)
                .applicationStatus(application.getApplicationStatus().toString())
                .applicationDate(application.getPossibleDate().getDate())
                .build();
    }
}
