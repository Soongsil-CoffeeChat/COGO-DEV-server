package com.soongsil.CoffeeChat.service;

import java.util.HashMap;
import java.util.Map;

import com.soongsil.CoffeeChat.entity.Introduction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.dto.MenteeJoinRequestDto;
import com.soongsil.CoffeeChat.dto.MentorJoinRequestDto;
import com.soongsil.CoffeeChat.dto.UserGetUpdateDto;
import com.soongsil.CoffeeChat.dto.UserJoinRequestDto;
import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.repository.MenteeRepository;
import com.soongsil.CoffeeChat.repository.Mentor.MentorRepository;
import com.soongsil.CoffeeChat.repository.User.UserRepository;
import com.soongsil.CoffeeChat.util.sms.SmsUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final MentorRepository mentorRepository;
	private final MenteeRepository menteeRepository;
	private final UserRepository userRepository;
	private final SmsUtil smsUtil;

	@Transactional
	public User saveUserInformation(String username, UserJoinRequestDto dto) {
		User user = userRepository.findByUsername(username);
		user.setName(dto.getName());
		user.setPhoneNum(dto.getPhoneNum());
		return userRepository.save(user);
	}

	@Transactional
	public Mentor saveMentorInformation(String username, MentorJoinRequestDto dto) {
		User user = userRepository.findByUsername(username);
		if (!user.getRole().equals("ROLE_ADMIN"))
			user.setRole("ROLE_MENTOR");
		Mentor mentor = Mentor.from(dto);
		user.setMentor(mentor);
		Introduction introduction=new Introduction();
		mentor.setIntroduction(introduction);
		return mentorRepository.save(mentor);
	}

	@Transactional
	public Mentee saveMenteeInformation(String username, MenteeJoinRequestDto dto) {
		User user = userRepository.findByUsername(username);
		if (!user.getRole().equals("ROLE_ADMIN"))
			user.setRole("ROLE_MENTEE");
		Mentee mentee = Mentee.from(dto);
		user.setMentee(mentee);
		return menteeRepository.save(mentee);
	}

	@Transactional
	public User saveUserPicture(String username, String picture) {
		User user = userRepository.findByUsername(username);
		user.setPicture(picture);
		return userRepository.save(user);
	}

	public ResponseEntity<Map<String, String>> getSmsCode(String to) {
		Map<String, String> response = new HashMap<>();
		String result = smsUtil.sendOne(to);
		if (result != null) {
			response.put("verificationCode", result);
			response.put("message", "Verification code sent successfully");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.put("message", "Failed to send verification code");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<User> saveUserPhone(String phone, String username) {
		User user = userRepository.findByUsername(username);
		user.setPhoneNum(phone);
		return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
	}

	public User saveUserEmail(String email, String username) {
		User user = userRepository.findByUsername(username);
		user.setEmail(email);
		return userRepository.save(user);
	}

	public User changeUserInfo(UserGetUpdateDto dto, String username) {
		User user = userRepository.findByUsername(username);
		user.setEmail(dto.getEmail());
		user.setPhoneNum(dto.getPhoneNum());
		return userRepository.save(user);
	}

	public UserGetUpdateDto findUserInfo(String username) {
		User user = userRepository.findByUsername(username);
		return UserGetUpdateDto.toDto(user);
	}
}
