package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.enums.RequestUri.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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
	@ApiResponse(responseCode = "201", description = "DTO형식으로 정보 반환")
	public ResponseEntity<ApiResponseGenerator<List<PossibleDateCreateGetResponseDto>>> addPossibleDate(
		Authentication authentication,
		@RequestBody List<PossibleDateCreateRequestDto> dtos) throws Exception {
		String username = getUserNameByAuthentication(authentication);

		List<PossibleDateCreateGetResponseDto> responseDtos = dtos.stream()
			.map(dto -> possibleDateService.createPossibleDate(dto, username))
			.collect(Collectors.toList());

		return ResponseEntity.created(URI.create(POSSIBLEDATE_URI)).body(
			ApiResponseGenerator.onSuccessCREATED(responseDtos)
		);
	}

	@GetMapping
	@Operation(summary = "멘토ID로 커피챗 가능시간 불러오기")
	@ApiResponse(responseCode = "200", description = "DTO LIST형식으로 정보 반환")
	public ResponseEntity<ApiResponseGenerator<List<PossibleDateCreateGetResponseDto>>> getPossibleDates(
		Authentication authentication
	) throws Exception {
		return ResponseEntity.ok().body(
			ApiResponseGenerator.onSuccessOK(
				possibleDateService.findPossibleDateListByMentor(getUserNameByAuthentication(authentication))
			)
		);
	}
}
