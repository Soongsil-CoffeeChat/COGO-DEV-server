package com.soongsil.CoffeeChat.domain.mentor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.soongsil.CoffeeChat.domain.mentor.enums.ClubEnum;
import com.soongsil.CoffeeChat.domain.mentor.enums.PartEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

public class MentorRequest {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MentorJoinRequest {
        private PartEnum part;
        private ClubEnum club;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MentorIntroductionUpdateRequest {

        @JsonProperty("introduction_title")
        private Optional<String> title = Optional.empty();

        @JsonProperty("introduction_description")
        private Optional<String> description = Optional.empty();

        @JsonProperty("introduction_answer1")
        private Optional<String> answer1 = Optional.empty();

        @JsonProperty("introduction_answer2")
        private Optional<String> answer2 = Optional.empty();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MentorUpdateRequest {
        private String mentorName;
        private String mentorPhoneNumber;
        private String mentorEmail;
    }
}
