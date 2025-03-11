package com.soongsil.CoffeeChat.dto;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MentorIntroductionUpdateRequestDto {

    @JsonProperty("introduction_title")
    private Optional<String> title = Optional.empty();

    @JsonProperty("introduction_description")
    private Optional<String> description = Optional.empty();

    @JsonProperty("introduction_answer1")
    private Optional<String> answer1 = Optional.empty();

    @JsonProperty("introduction_answer2")
    private Optional<String> answer2 = Optional.empty();
}
