package com.soongsil.CoffeeChat.controller.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.soongsil.CoffeeChat.controller.exception.CustomException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@JsonPropertyOrder({"statusCode", "message", "content"})
@AllArgsConstructor
public class ApiResponseGenerator {

	@JsonProperty("statusCode")
	@NonNull
	private final String statusCode;

	@JsonProperty("message")
	@NonNull
	private final String message;

	public static ApiResponseGenerator onFailure(
		CustomException customException) {
		return new ApiResponseGenerator(
			customException.getErrorCode().toString(),
			customException.getErrorMessage()
		);
	}
}
