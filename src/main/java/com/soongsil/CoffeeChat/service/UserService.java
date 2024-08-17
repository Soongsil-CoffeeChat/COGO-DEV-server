package com.soongsil.CoffeeChat.service;

import com.soongsil.CoffeeChat.entity.PossibleDate;
import com.soongsil.CoffeeChat.util.sms.SmsUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.dto.CreateMenteeRequest;
import com.soongsil.CoffeeChat.dto.CreateMentorRequest;
import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.repository.MenteeRepository;
import com.soongsil.CoffeeChat.repository.Mentor.MentorRepository;
import com.soongsil.CoffeeChat.repository.User.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
	private final MentorRepository mentorRepository;
	private final MenteeRepository menteeRepository;
	private final UserRepository userRepository;
	private final SmsUtil smsUtil;

	@Transactional
	public Mentor saveMentorInformation(String username, CreateMentorRequest dto) {
		User user = userRepository.findByUsername(username);
		if(!user.getRole().equals("ROLE_ADMIN")) user.setRole("ROLE_MENTOR");
		Mentor mentor = Mentor.from(dto);
		user.setMentor(mentor);
		return mentorRepository.save(mentor);
	}

	@Transactional
	public Mentee saveMenteeInformation(String username, CreateMenteeRequest dto) {
		User user = userRepository.findByUsername(username);
		if(!user.getRole().equals("ROLE_ADMIN")) user.setRole("ROLE_MENTEE");
		Mentee mentee = Mentee.from(dto);
		user.setMentee(mentee);
		//이메일 사용자가 지정한값으로 변경해야함
		user.setEmail(dto.getEmail());
		return menteeRepository.save(mentee);
	}

	@Transactional
	public User saveUserPicture(String username, String picture){
		User user = userRepository.findByUsername(username);
		user.setPicture(picture);
		return userRepository.save(user);
	}

	public ResponseEntity<Map<String, String>> getSmsCode(String to){
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

	public ResponseEntity<User> saveUserPhone(String phone, String username){
		User user=userRepository.findByUsername(username);
		user.setPhone(phone);
		return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
	}

	@Transactional
	public void createUsersAndMentors() {
		List<User> users = new ArrayList<>();
		List<Mentor> mentors = new ArrayList<>();

		for (int i = 1; i <= 500; i++) {
			Mentor mentor = Mentor.builder()
					.phoneNum("010-0000-" + String.format("%04d", i))
					.birth("1990-01-01")
					.part("BE")
					.build();

			// PossibleDate 생성 및 Mentor에 추가
			PossibleDate possibleDate1 = PossibleDate.builder()
					.mentor(mentor)
					.date(LocalDate.of(2024, 8, 18))  // 예시 날짜
					.startTime(LocalTime.of(10, 0))   // 예시 시작 시간
					.endTime(LocalTime.of(12, 0))     // 예시 종료 시간
					.build();

			PossibleDate possibleDate2 = PossibleDate.builder()
					.mentor(mentor)
					.date(LocalDate.of(2024, 8, 19))  // 예시 날짜
					.startTime(LocalTime.of(14, 0))   // 예시 시작 시간
					.endTime(LocalTime.of(16, 0))     // 예시 종료 시간
					.build();

			mentor.addPossibleDate(possibleDate1);
			mentor.addPossibleDate(possibleDate2);

			User user = new User();
			user.setPhone("010-1111-" + String.format("%04d", i));
			user.setUsername("user" + i);
			user.setName("User Name " + i);
			user.setEmail("user" + i + "@example.com");
			user.setRole("USER");
			user.setMentor(mentor); // User와 Mentor 연결

			mentors.add(mentor);
			users.add(user);
		}

		mentorRepository.saveAll(mentors);
		userRepository.saveAll(users);
	}

}
