package com.soongsil.CoffeeChat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.soongsil.CoffeeChat.enums.ClubEnum;
import com.soongsil.CoffeeChat.enums.PartEnum;

import lombok.*;

public class MentorResponse {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MentorIntroductionGetUpdateResponse {

        @JsonProperty("introduction_title")
        @NonNull
        private String title;

        @JsonProperty("introduction_description")
        @NonNull
        private String description;

        @JsonProperty("introduction_answer1")
        @NonNull
        private String answer1;

        @JsonProperty("introduction_answer2")
        @NonNull
        private String answer2;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MentorListResponse {
        private String picture;
        private String mentorName;
        private PartEnum part;
        private ClubEnum club;
        private String username;
        private Long mentorId;
        private String title;
        private String description;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MentorGetUpdateDetailResponse {
        Long mentorId;
        String mentorName;
        PartEnum part;
        ClubEnum club;
        String introductionTitle;
        String introductionDescription;
        String introductionAnswer1;
        String introductionAnswer2;
        String imageUrl;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MentorInfoResponse {
        private PartEnum part;
        private ClubEnum club;
        private Long introductionId;
        private boolean isNewAccount;
    }
}
