package com.soongsil.CoffeeChat.global.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Getter
@JsonPropertyOrder({"statusCode", "message", "content"})
@AllArgsConstructor
public class ApiResponse<T> {

    @JsonProperty("statusCode")
    @NonNull
    private final String statusCode;

    @JsonProperty("message")
    @NonNull
    private final String message;

    @JsonProperty("content")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T content;

    public static <T> ApiResponse<T> onSuccess(HttpStatus httpStatus, String message, T content) {
        return new ApiResponse<>(String.valueOf(httpStatus.value()), message, content);
    }

    public static <T> ApiResponse<T> onSuccessOK(T content) {
        return new ApiResponse<>(
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK.name(), content);
    }

    public static <T> ApiResponse<T> onSuccessCREATED(T content) {
        return new ApiResponse<>(
                String.valueOf(HttpStatus.CREATED.value()), HttpStatus.CREATED.name(), content);
    }
}
