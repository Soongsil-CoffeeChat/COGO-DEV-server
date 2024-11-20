package com.soongsil.CoffeeChat.controller;

import java.net.URI;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.soongsil.CoffeeChat.controller.handler.ApiResponseGenerator;
import com.soongsil.CoffeeChat.dto.Oauth.CustomOAuth2User;
import com.soongsil.CoffeeChat.service.S3Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v2/s3")
@Tag(name = "S3", description = "S3 관련 api")
public class S3Controller {
	private final S3Service s3Service;

	private String getUserNameByAuthentication(Authentication authentication) throws Exception {
		CustomOAuth2User principal = (CustomOAuth2User)authentication.getPrincipal();
		if (principal == null)
			throw new Exception(); //TODO : Exception 만들기
		return principal.getUsername();
	}

	@PostMapping(value = "/{directory}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "사진저장")
	@ApiResponse(responseCode = "201", description = "사진 저장됨")
	public ResponseEntity<ApiResponseGenerator<Map<String, String>>> saveImageInS3(Authentication authentication,
		@RequestPart MultipartFile image, @PathVariable("directory") String directory) throws Exception {
		String savedUrl = s3Service.saveFile(directory, getUserNameByAuthentication(authentication),
			image);
		return ResponseEntity.created(URI.create(savedUrl)).body(
			ApiResponseGenerator.onSuccessCREATED(
				Map.of(
					"savedUrl", savedUrl
				)
			)
		);
	}
}
