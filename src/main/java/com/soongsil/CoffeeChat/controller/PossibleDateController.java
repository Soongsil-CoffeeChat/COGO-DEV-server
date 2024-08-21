package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.enums.RequestUri.*;
import static org.springframework.http.HttpStatus.*;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.controller.handler.ApiResponseGenerator;
import com.soongsil.CoffeeChat.dto.Oauth.CustomOAuth2User;
import com.soongsil.CoffeeChat.dto.PossibleDateCreateGetResponseDto;
import com.soongsil.CoffeeChat.dto.PossibleDateCreateRequestDto;
import com.soongsil.CoffeeChat.service.PossibleDateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(POSSIBLEDATE_URI)
@RequiredArgsConstructor
@Tag(name = "POSSIBLEDATE", description = "커피챗 시간 관련 api")
public class PossibleDateController {
	private final PossibleDateService possibleDateService;

	private String getUserNameByAuthentication(Authentication authentication) throws Exception {
		CustomOAuth2User principal = (CustomOAuth2User)authentication.getPrincipal();
		if (principal == null)
			throw new Exception(); //TODO : Exception 만들기
		return principal.getUsername();
	}

	@PostMapping()
	@Operation(summary = "멘토가 직접 커피챗 가능시간 추가하기")
	@ApiResponse(responseCode = "200", description = "DTO형식으로 정보 반환")
	public ApiResponseGenerator<PossibleDateCreateGetResponseDto> addPossibleDate(
		Authentication authentication,
		@RequestBody PossibleDateCreateRequestDto dto) throws Exception {
		return ApiResponseGenerator.onSuccess(
			CREATED,
			CREATED.getReasonPhrase(),
			possibleDateService.createPossibleDate(dto, getUserNameByAuthentication(authentication))
		);
	}
}
