package com.soongsil.CoffeeChat.domain.mentor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.soongsil.CoffeeChat.domain.mentor.enums.ClubEnum;
import com.soongsil.CoffeeChat.domain.mentor.enums.PartEnum;

import lombok.*;

public class MentorResponse {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MentorIntroductionResponse {

        @JsonProperty("introduction_title")
        private String title;

        @JsonProperty("introduction_description")
        private String description;

        @JsonProperty("introduction_answer1")
        private String answer1;

        @JsonProperty("introduction_answer2")
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
    public static class MentorDetailResponse {
        private Long mentorId;
        private String mentorName;
        private PartEnum part;
        private ClubEnum club;
        private String introductionTitle;
        private String introductionDescription;
        private String introductionAnswer1;
        private String introductionAnswer2;
        private String imageUrl;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MentorInfoResponse {
        private PartEnum part;
        private ClubEnum club;
    }
}
