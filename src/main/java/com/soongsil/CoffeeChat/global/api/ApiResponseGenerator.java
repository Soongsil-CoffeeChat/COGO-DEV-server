package com.soongsil.CoffeeChat.global.api;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@JsonPropertyOrder({"statusCode", "message", "content"})
@AllArgsConstructor
public class ApiResponseGenerator<T> {

    @JsonProperty("statusCode")
    @NonNull
    private final String statusCode;

    @JsonProperty("message")
    @NonNull
    private final String message;

    @JsonProperty("content")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T content;

    public static <T> ApiResponseGenerator<T> onSuccess(
            HttpStatus httpStatus, String message, T content) {
        return new ApiResponseGenerator<>(String.valueOf(httpStatus.value()), message, content);
    }

    public static <T> ApiResponseGenerator<T> onSuccessOK(T content) {
        return new ApiResponseGenerator<>(
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK.name(), content);
    }

    public static <T> ApiResponseGenerator<T> onSuccessCREATED(T content) {
        return new ApiResponseGenerator<>(
                String.valueOf(HttpStatus.CREATED.value()), HttpStatus.CREATED.name(), content);
    }
}
