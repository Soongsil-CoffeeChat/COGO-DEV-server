package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.enums.RequestUri.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.dto.MentorGetListResponseDto;
import com.soongsil.CoffeeChat.dto.MentorGetUpdateDetailDto;
import com.soongsil.CoffeeChat.dto.MentorIntroductionUpdateRequestDto;
import com.soongsil.CoffeeChat.dto.MentorIntroductionUpdateResponseDto;
import com.soongsil.CoffeeChat.dto.MentorUpdateRequestDto;
import com.soongsil.CoffeeChat.dto.Oauth.CustomOAuth2User;
import com.soongsil.CoffeeChat.dto.PossibleDateCreateGetResponseDto;
import com.soongsil.CoffeeChat.enums.ClubEnum;
import com.soongsil.CoffeeChat.enums.PartEnum;
import com.soongsil.CoffeeChat.service.MentorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping(MENTOR_URI)
@RestController
@Tag(name = "MENTOR", description = "멘토 관련 api")
public class MentorController {

	private final MentorService mentorService;

	public MentorController(MentorService mentorService) {
		this.mentorService = mentorService;
	}

	private String getUserNameByAuthentication(Authentication authentication) throws Exception {
		CustomOAuth2User principal = (CustomOAuth2User)authentication.getPrincipal();
		if (principal == null)
			throw new Exception(); //TODO : Exception 만들기
		return principal.getUsername();
	}

	@PatchMapping
	@Operation(summary = "멘토의 세부 정보 수정")
	@ApiResponse(responseCode = "200", description = "변경된 멘토 세부 정보를 반환")
	public ResponseEntity<MentorGetUpdateDetailDto> updateMentorInfo(
		Authentication authentication,
		@RequestBody MentorUpdateRequestDto mentorUpdateRequestDto
	) {
		return ResponseEntity.ok()
			.body(
				mentorService.updateMentorInfo(
					((CustomOAuth2User)authentication.getPrincipal()).getUsername(),
					mentorUpdateRequestDto)
			);
	}

	@GetMapping("/{mentorId}")
	@Operation(summary = "멘토 상세 정보 조회")
	@ApiResponse(responseCode = "200", description = "멘토 상세 정보 DTO 반환")
	public ResponseEntity<MentorGetUpdateDetailDto> getMentorInfo(@PathVariable("mentorId") Long mentorId) {
		return ResponseEntity.ok().body(mentorService.getMentorDtoByIdWithJoin(mentorId));
	}

	@PatchMapping("/{mentorId}/introductions")
	@Operation(summary = "멘토 자기소개 입력")
	@ApiResponse(responseCode = "200", description = "자기소개의 수정된 버전을 반환")
	public ResponseEntity<MentorIntroductionUpdateResponseDto> updateMentoIntroduction(
		@PathVariable("mentorId") Long mentorId,
		@RequestBody MentorIntroductionUpdateRequestDto dto
	) {
		return ResponseEntity.ok().body(mentorService.updateMentorIntroduction(mentorId, dto));
	}

	@GetMapping("/part")
	@Operation(summary = "파트별 멘토 리스트 가져오기")
	@ApiResponse(responseCode = "200", description = "DTO LIST형식으로 정보 반환")
	public ResponseEntity<List<MentorGetListResponseDto>> getMentorListByPart(@RequestParam("part") PartEnum part) {
		return ResponseEntity.ok().body(mentorService.getMentorDtoListByPart(part));
	}

	@GetMapping("/club")
	@Operation(summary = "동아리별 멘토 리스트 가져오기")
	@ApiResponse(responseCode = "200", description = "DTO LIST형식으로 정보 반환")
	public ResponseEntity<List<MentorGetListResponseDto>> getMentorListByClub(@RequestParam("club") ClubEnum club) {
		return ResponseEntity.ok().body(mentorService.getMentorDtoListByClub(club));
	}

	@GetMapping("/part/club")
	@Operation(summary = "파트+동아리별 멘토 리스트 가져오기")
	@ApiResponse(responseCode = "200", description = "DTO LIST형식으로 정보 반환")
	public ResponseEntity<List<MentorGetListResponseDto>> getMentorListByClub(@RequestParam("part") PartEnum part,
		@RequestParam("club") ClubEnum club) {
		return ResponseEntity.ok().body(mentorService.getMentorDtoListByPartAndClub(part, club));
	}

	@GetMapping("/{mentorId}/possibleDates")
	@Operation(summary = "멘토ID로 커피챗가능시간 불러오기")
	@ApiResponse(responseCode = "200", description = "DTO LIST형식으로 정보 반환")
	public ResponseEntity<List<PossibleDateCreateGetResponseDto>> getPossibleDates(
		@PathVariable("mentorId") Long mentorId) {
		return ResponseEntity.ok().body(mentorService.findPossibleDateListByMentor(mentorId));
	}
}
