package com.soongsil.CoffeeChat.controller.handler;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.soongsil.CoffeeChat.controller.exception.CustomException;

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
		HttpStatus httpStatus,
		String message,
		T content
	) {
		return new ApiResponseGenerator<>(
			String.valueOf(httpStatus.value()),
			message,
			content
		);
	}

	public static <T> ApiResponseGenerator<T> onSuccessOK(
		T content
	) {
		return new ApiResponseGenerator<>(
			HttpStatus.OK.toString(),
			HttpStatus.OK.getReasonPhrase(),
			content
		);
	}

	public static <T> ApiResponseGenerator<T> onFailure(
		CustomException customException) {
		return new ApiResponseGenerator<>(
			customException.getErrorCode().toString(),
			customException.getErrorMessage(),
			null
		);
	}

	public static <T> ApiResponseGenerator<T> onFailure(
		CustomException customException, T content) {
		return new ApiResponseGenerator<>(
			customException.getErrorCode().toString(),
			customException.getErrorMessage(),
			content
		);
	}
}
