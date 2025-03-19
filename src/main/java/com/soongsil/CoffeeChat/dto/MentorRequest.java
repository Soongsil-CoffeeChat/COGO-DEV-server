package com.soongsil.CoffeeChat.dto;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.soongsil.CoffeeChat.enums.ClubEnum;
import com.soongsil.CoffeeChat.enums.PartEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
