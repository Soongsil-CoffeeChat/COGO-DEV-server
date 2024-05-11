package com.soongsil.CoffeeChat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.soongsil.CoffeeChat.dto.CustomOAuth2User;
import com.soongsil.CoffeeChat.service.S3Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/s3")
@Tag(name = "S3", description = "S3 관련 api")
public class S3Controller {
	private final S3Service s3Service;

	private String getUserNameByAuthentication(Authentication authentication) throws Exception {
		CustomOAuth2User principal = (CustomOAuth2User)authentication.getPrincipal();
		if (principal == null)
			throw new Exception(); //TODO : Exception 만들기
		return principal.getUsername();
	}
	@PostMapping("/{directory}")
	@Operation(summary = "사진저장")
	@ApiResponse(responseCode = "200", description = "사진 저장됨")
	public ResponseEntity<String> saveImageInS3(Authentication authentication,
		@RequestPart MultipartFile image, @PathVariable("directory") String directory) throws Exception {
		return ResponseEntity.ok().body(s3Service.saveFile(directory, getUserNameByAuthentication(authentication),
			image));
	}
}
