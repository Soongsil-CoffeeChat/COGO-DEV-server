package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.enums.RequestUri.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.dto.ApplicationCreateRequestDto;
import com.soongsil.CoffeeChat.dto.ApplicationCreateResponseDto;
import com.soongsil.CoffeeChat.dto.ApplicationGetResponseDto;
import com.soongsil.CoffeeChat.dto.Oauth.CustomOAuth2User;
import com.soongsil.CoffeeChat.service.ApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(APPLICATION_URI)
@RequiredArgsConstructor
@Tag(name = "APPLICATION", description = "Application 관련 api")
public class ApplicationController {

	private final ApplicationService applicationService;

	@PostMapping
	@Operation(summary = "COGO 신청하기")
	@ApiResponse(responseCode = "200", description = "COGO 기본 정보 반환")
	public ResponseEntity<ApplicationCreateResponseDto> createApplication(
		Authentication authentication,
		@RequestBody ApplicationCreateRequestDto request
	) throws Exception {
		return ResponseEntity.ok()
			.body(applicationService.createApplication(request,
				((CustomOAuth2User)authentication.getPrincipal()).getUsername())
			);
	}

	@GetMapping("/{applicationId}")
	@Operation(summary = "특정 COGO 조회")
	@ApiResponse(responseCode = "200", description = "COGO 세부 정보 반환")
	public ResponseEntity<ApplicationGetResponseDto> getApplication(
		@PathVariable Long applicationId
	) {
		return ResponseEntity.ok()
			.body(applicationService.getApplication(
				applicationId
			));
	}

	@GetMapping
	@Operation(summary = "신청 받은 COGO 전체 조회")
	@ApiResponse(responseCode = "200", description = "COGO LIST 반환")
	public ResponseEntity<List<ApplicationGetResponseDto>> getApplications(
		Authentication authentication
	) {
		return ResponseEntity.ok()
			.body(applicationService.getApplications(
				((CustomOAuth2User)authentication.getPrincipal()).getUsername())
			);
	}
}
