package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.enums.RequestUri.*;

import java.util.List;

import com.soongsil.CoffeeChat.dto.MentorUpdateRequestDto;
import com.soongsil.CoffeeChat.dto.Oauth.CustomOAuth2User;
import com.soongsil.CoffeeChat.dto.ResponseMentorInfo;
import com.soongsil.CoffeeChat.entity.Mentor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.dto.PossibleDateRequestDto;
import com.soongsil.CoffeeChat.dto.ResponseMentorListInfo;
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

	@GetMapping("/{mentorId}")
	@Operation(summary = "멘토 상세 정보 조회")
	@ApiResponse(responseCode = "200", description = "멘토 상세 정보 DTO 반환")
	public ResponseEntity<ResponseMentorInfo> getMentorInfo(@PathVariable("mentorId") Long mentorId) {
		return ResponseEntity.ok().body(mentorService.getMentorDtobyId(mentorId));
	}

	@GetMapping("/{part}")
	@Operation(summary = "파트별 멘토 리스트 가져오기")
	@ApiResponse(responseCode = "200", description = "DTO LIST형식으로 정보 반환")
	public ResponseEntity<List<ResponseMentorListInfo>> getMentorListByPart(@PathVariable("part") int part) {
		return ResponseEntity.ok().body(mentorService.getMentorDtoListByPart(part));
	}

	@GetMapping("/{club}")
	@Operation(summary = "동아리별 멘토 리스트 가져오기")
	@ApiResponse(responseCode = "200", description = "DTO LIST형식으로 정보 반환")
	public ResponseEntity<List<ResponseMentorListInfo>> getMentorListByClub(@PathVariable("club") int club) {
		return ResponseEntity.ok().body(mentorService.getMentorDtoListByClub(club));
	}

	@GetMapping("/{part}/{club}")
	@Operation(summary = "파트+동아리별 멘토 리스트 가져오기")
	@ApiResponse(responseCode = "200", description = "DTO LIST형식으로 정보 반환")
	public ResponseEntity<List<ResponseMentorListInfo>> getMentorListByClub(@PathVariable("part") int part,
		@PathVariable("club") int club) {
		return ResponseEntity.ok().body(mentorService.getMentorDtoListByPartAndClub(part, club));
	}

	@GetMapping("/{mentorId}/possibleDates")
	@Operation(summary = "멘토ID로 커피챗가능시간 불러오기")
	@ApiResponse(responseCode = "200", description = "DTO LIST형식으로 정보 반환")
	public ResponseEntity<List<PossibleDateRequestDto>> getPossibleDates(@PathVariable("mentorId") Long mentorId) {
		return ResponseEntity.ok().body(mentorService.findPossibleDateListByMentor(mentorId));
	}

	@PatchMapping
	@Operation(summary = "멘토의 세부 정보 수정")
	@ApiResponse(responseCode = "200", description = "변경된 멘토 세부 정보를 반환")
	public ResponseEntity<ResponseMentorInfo> updateMentorInfo(
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
}
