package com.soongsil.CoffeeChat.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.soongsil.CoffeeChat.controller.exception.CustomException;
import com.soongsil.CoffeeChat.dto.*;
import com.soongsil.CoffeeChat.dto.UserController.MenteeInfoDto;
import com.soongsil.CoffeeChat.dto.UserController.MentorInfoDto;
import com.soongsil.CoffeeChat.dto.UserController.UserInfoDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.entity.Introduction;
import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.repository.MenteeRepository;
import com.soongsil.CoffeeChat.repository.Mentor.MentorRepository;
import com.soongsil.CoffeeChat.repository.User.UserRepository;
import com.soongsil.CoffeeChat.util.sms.SmsUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.soongsil.CoffeeChat.controller.exception.enums.PossibleDateErrorCode.POSSIBLE_DATE_NOT_FOUND;
import static com.soongsil.CoffeeChat.controller.exception.enums.UserErrorCode.USER_NOT_FOUND;
import static com.soongsil.CoffeeChat.controller.exception.enums.UserErrorCode.USER_SMS_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
	private final MentorRepository mentorRepository;
	private final MenteeRepository menteeRepository;
	private final UserRepository userRepository;
	private final SmsUtil smsUtil;

	private User findUserByUsername(String username){
        return userRepository.findByUsername(username)
				.orElseThrow(() -> new CustomException(
						USER_NOT_FOUND.getHttpStatusCode(),
						USER_NOT_FOUND.getErrorMessage())
				);
	}

	@Transactional
	public UserInfoDto saveUserInformation(String username, UserJoinRequestDto dto) {
		User user = findUserByUsername(username);
		user.setName(dto.getName());
		user.setPhoneNum(dto.getPhoneNum());
		return UserInfoDto.toDto(userRepository.save(user));
	}

	@Transactional
	public MentorInfoDto saveMentorInformation(String username, MentorJoinRequestDto dto) throws Exception{
		User user = findUserByUsername(username);
		log.info("[*] User name: " + user.getUsername());
		log.info("[*] User Role before: " + user.getRole());
		if (!user.getRole().equals("ROLE_ADMIN")) {
			user.setRole("ROLE_MENTOR");
		}
		log.info("[*] User Role after: " + user.getRole());
		Mentor mentor = Mentor.from(dto);
		user.setMentor(mentor);
		Introduction introduction = new Introduction();
		mentor.setIntroduction(introduction);
		return MentorInfoDto.toDto(mentorRepository.save(mentor));
	}

	@Transactional
	public MenteeInfoDto saveMenteeInformation(String username, MenteeJoinRequestDto dto) {
		User user = findUserByUsername(username);
		if (!user.getRole().equals("ROLE_ADMIN"))
			user.setRole("ROLE_MENTEE");
		Mentee mentee = Mentee.from(dto);
		user.setMentee(mentee);
		return MenteeInfoDto.toDto(menteeRepository.save(mentee));
	}

	@Transactional
	public UserInfoDto saveUserPicture(String username, String picture) {
		User user = findUserByUsername(username);
		user.setPicture(picture);
		return UserInfoDto.toDto(userRepository.save(user));
	}

	public Map<String, String> getSmsCode(String to) {
		Map<String, String> response = new HashMap<>();
		String result = smsUtil.sendOne(to);
		if (result != null) {
			response.put("verificationCode", result);
			response.put("message", "Verification code sent successfully");
			return response;
		} else {
			throw new CustomException(
					USER_SMS_ERROR.getHttpStatusCode(),
					USER_SMS_ERROR.getErrorMessage()
			);
		}
	}

	public UserInfoDto saveUserPhone(String phone, String username) {
		User user = findUserByUsername(username);
		user.setPhoneNum(phone);
		return UserInfoDto.toDto(userRepository.save(user));
	}

	public UserInfoDto saveUserEmail(String email, String username) {
		User user = findUserByUsername(username);
		user.setEmail(email);
		return UserInfoDto.toDto(userRepository.save(user));
	}

	public UserInfoDto changeUserInfo(UserGetUpdateDto dto, String username) {
		User user = findUserByUsername(username);
		user.setEmail(dto.getEmail());
		user.setPhoneNum(dto.getPhoneNum());
		return UserInfoDto.toDto(userRepository.save(user));
	}

	public UserGetUpdateDto findUserInfo(String username) {
		User user = findUserByUsername(username);
		return UserGetUpdateDto.toDto(user);
	}

}
