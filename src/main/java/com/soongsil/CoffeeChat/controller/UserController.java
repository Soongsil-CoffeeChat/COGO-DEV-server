package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.enums.RequestUri.*;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.controller.handler.ApiResponseGenerator;
import com.soongsil.CoffeeChat.dto.MenteeJoinRequestDto;
import com.soongsil.CoffeeChat.dto.MentorJoinRequestDto;
import com.soongsil.CoffeeChat.dto.Oauth.CustomOAuth2User;
import com.soongsil.CoffeeChat.dto.UserGetUpdateDto;
import com.soongsil.CoffeeChat.dto.UserJoinRequestDto;
import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.repository.User.UserRepository;
import com.soongsil.CoffeeChat.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(USER_URI)
@RequiredArgsConstructor
@Tag(name = "USER", description = "유저 관련 api")
public class UserController {
	private final UserService userService;
	private final UserRepository userRepository;

	// TODO: 이 getUserNameByAuthentication 함수가 모든 controller에서 중복되므로 util로 빼기
	private String getUserNameByAuthentication(Authentication authentication) throws Exception {
		CustomOAuth2User principal = (CustomOAuth2User)authentication.getPrincipal();
		if (principal == null)
			throw new Exception(); //TODO : Exception 만들기
		return principal.getUsername();
	}

	@PostMapping()
	@Operation(summary = "기본정보 기입")
	@ApiResponse(responseCode = "200", description = "성공!")
	public ResponseEntity<User> joinWithMentor(Authentication authentication,
		@RequestBody UserJoinRequestDto dto) throws Exception {
		// TODO: 엔티티 반환 X, DTO 만들어서 반환하기
		// TODO: ApiResponseGenerator 잘 사용하기 (statusCode 200, 201은 많이 써서 함수로 아예 만들어놨음)
		return ResponseEntity.status(HttpStatus.CREATED).body(
			userService.saveUserInformation(getUserNameByAuthentication(authentication), dto)
		);
	}

	@PostMapping("/mentor")
	@Operation(summary = "멘토로 가입하기!")
	@ApiResponse(responseCode = "200", description = "성공!")
	public ResponseEntity<Mentor> joinWithMentor(Authentication authentication,
		@RequestBody MentorJoinRequestDto dto) throws Exception {
		// TODO: 엔티티 반환 X, DTO 만들어서 반환하기
		// TODO: ApiResponseGenerator 잘 사용하기 (statusCode 200, 201은 많이 써서 함수로 아예 만들어놨음)
		return ResponseEntity.status(HttpStatus.CREATED).body(
			userService.saveMentorInformation(getUserNameByAuthentication(authentication), dto)
		);
	}

	@PostMapping("/mentee")
	@Operation(summary = "멘티로 가입하기!")
	@ApiResponse(responseCode = "200", description = "성공!")
	public ResponseEntity<Mentee> joinWithMentee(Authentication authentication,
		@RequestBody MenteeJoinRequestDto dto) throws Exception {
		// TODO: 엔티티 반환 X, DTO 만들어서 반환하기
		// TODO: ApiResponseGenerator 잘 사용하기 (statusCode 200, 201은 많이 써서 함수로 아예 만들어놨음)
		return ResponseEntity.status(HttpStatus.CREATED).body(
			userService.saveMenteeInformation(getUserNameByAuthentication(authentication), dto)
		);
	}

	@PutMapping("/picture")
	@Operation(summary = "이미지 저장하기")
	@ApiResponse(responseCode = "200", description = "성공!")
	public ResponseEntity<User> saveUserPicture(Authentication authentication,
		@RequestBody String picture) throws Exception {
		// TODO: 엔티티 반환 X, DTO 만들어서 반환하기
		// TODO: ApiResponseGenerator 잘 사용하기 (statusCode 200, 201은 많이 써서 함수로 아예 만들어놨음)
		return ResponseEntity.ok(userService.saveUserPicture(getUserNameByAuthentication(authentication), picture));
	}

	@GetMapping("/sms")
	@Operation(summary = "SMS 인증 요청하기", description = "전화번호로 SMS 인증번호를 요청합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "인증 요청 성공",
			content = @Content(mediaType = "application/json", schema = @Schema(example = "{\n\"verificationCode\": \"1234\",\n\"message\": \"Verification code sent successfully\"\n}"))),
		@ApiResponse(responseCode = "400", description = "인증번호 전송 실패",
			content = @Content(mediaType = "application/json", schema = @Schema(example = "{\n\"message\": \"Failed to send verification code\"\n}")))
	})
	public ResponseEntity<Map<String, String>> getSmsCode(Authentication authentication,
		@RequestParam("phoneNum") String phoneNum) {
		// TODO: ApiResponseGenerator 잘 사용하기 (statusCode 200, 201은 많이 써서 함수로 아예 만들어놨음)
		// TODO: Map.of() 함수 사용해서 코드 간결화하면 더 좋을 듯 (EmailController 참조)
		return userService.getSmsCode(phoneNum);
	}

	@PatchMapping("/phone")
	@Operation(summary = "번호 저장하기")
	@ApiResponse(responseCode = "200", description = "성공!")
	public ResponseEntity<User> saveUserPhone(Authentication authentication,
		@RequestParam("phoneNum") String phoneNum) throws Exception {
		// TODO: 엔티티 반환 X, DTO 만들어서 반환하기
		// TODO: ApiResponseGenerator 잘 사용하기 (statusCode 200, 201은 많이 써서 함수로 아예 만들어놨음)
		return userService.saveUserPhone(phoneNum, getUserNameByAuthentication(authentication));
	}

	@PatchMapping("/email")
	@Operation(summary = "메일 저장하기")
	@ApiResponse(responseCode = "200", description = "성공!")
	public ResponseEntity<User> saveUserEmail(Authentication authentication,
		@RequestParam("email") String email) throws Exception {
		// TODO: 엔티티 반환 X, DTO 만들어서 반환하기
		// TODO: ApiResponseGenerator 잘 사용하기 (statusCode 200, 201은 많이 써서 함수로 아예 만들어놨음)
		return new ResponseEntity<>(userService.saveUserEmail(email, getUserNameByAuthentication(authentication)),
			HttpStatus.OK);
	}

	@PatchMapping()
	@Operation(summary = "사용자 정보 수정")
	@ApiResponse(responseCode = "200", description = "성공!")
	public ResponseEntity<User> saveUserEmail(Authentication authentication,
		@RequestBody UserGetUpdateDto dto) throws Exception {
		// TODO: 엔티티 반환 X, DTO 만들어서 반환하기
		// TODO: ApiResponseGenerator 잘 사용하기 (statusCode 200, 201은 많이 써서 함수로 아예 만들어놨음)
		return new ResponseEntity<>(userService.changeUserInfo(dto, getUserNameByAuthentication(authentication)),
			HttpStatus.OK);
	}

	@GetMapping()
	@Operation(summary = "기본정보 조회")
	@ApiResponse(responseCode = "200", description = "성공!")
	public ResponseEntity<ApiResponseGenerator<UserGetUpdateDto>> getUserInfo(Authentication authentication) throws
		Exception {
		return ResponseEntity.ok().body(
			ApiResponseGenerator.onSuccessOK(
				userService.findUserInfo(getUserNameByAuthentication(authentication))
			)
		);
	}

}
