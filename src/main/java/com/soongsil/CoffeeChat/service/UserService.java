package com.soongsil.CoffeeChat.service;

import static com.soongsil.CoffeeChat.global.exception.GlobalErrorCode.USER_SMS_ERROR;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.dto.*;
import com.soongsil.CoffeeChat.dto.MenteeRequest.*;
import com.soongsil.CoffeeChat.dto.MenteeResponse.*;
import com.soongsil.CoffeeChat.dto.MentorRequest.*;
import com.soongsil.CoffeeChat.dto.MentorResponse.*;
import com.soongsil.CoffeeChat.dto.UserRequest.*;
import com.soongsil.CoffeeChat.dto.UserResponse.*;
import com.soongsil.CoffeeChat.entity.Introduction;
import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import com.soongsil.CoffeeChat.global.security.dto.MobileUserDto;
import com.soongsil.CoffeeChat.infra.sms.SmsUtil;
import com.soongsil.CoffeeChat.repository.MenteeRepository;
import com.soongsil.CoffeeChat.repository.Mentor.MentorRepository;
import com.soongsil.CoffeeChat.repository.User.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final UserRepository userRepository;
    private final SmsUtil smsUtil;

    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public UserInfoResponse saveUserInformation(String username, UserJoinRequest dto) {
        User user = findUserByUsername(username);
        user.setName(dto.getName());
        user.setPhoneNum(dto.getPhoneNum());
        return UserConverter.toResponse(userRepository.save(user));
    }

    @Transactional
    public MentorInfoResponse saveMentorInformation(String username, MentorJoinRequest dto) {
        User user = findUserByUsername(username);
        log.info("[*] User name: " + user.getUsername());
        log.info("[*] User Role before: " + user.getRole());
        if (!user.getRole().equals("ROLE_ADMIN")) {
            user.setRole("ROLE_MENTOR");
        }
        log.info("[*] User Role after: " + user.getRole());
        Mentor mentor = MentorConverter.toEntity(dto);
        user.setMentor(mentor);
        Introduction introduction = new Introduction();
        mentor.setIntroduction(introduction);
        return MentorConverter.toResponse(mentorRepository.save(mentor));
    }

    @Transactional
    public MenteeInfoResponse saveMenteeInformation(String username, MenteeJoinRequest dto) {
        User user = findUserByUsername(username);
        if (!user.getRole().equals("ROLE_ADMIN")) user.setRole("ROLE_MENTEE");
        Mentee mentee = MenteeConverter.toEntity(dto);
        user.setMentee(mentee);
        return MenteeConverter.toResponse(menteeRepository.save(mentee));
    }

    @Transactional
    public UserInfoResponse saveUserPicture(String username, String picture) {
        User user = findUserByUsername(username);
        user.setPicture(picture);
        return UserConverter.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public Map<String, String> getSmsCode(String to) {
        Map<String, String> response = new HashMap<>();
        String result = smsUtil.sendOne(to);
        if (result != null) {
            response.put("verificationCode", result);
            response.put("message", "Verification code sent successfully");
            return response;
        } else {
            throw new GlobalException(USER_SMS_ERROR);
        }
    }

    @Transactional
    public PhoneNumUpdateDto saveUserPhone(String phone, String username) {
        User user = findUserByUsername(username);
        user.setPhoneNum(phone);
        userRepository.save(user);
        return PhoneNumUpdateDto.builder()
                .name(user.getName())
                .phoneNum(user.getPhoneNum())
                .isNewAccount(true)
                .build();
    }

    @Transactional
    public UserInfoResponse saveUserEmail(String email, String username) {
        User user = findUserByUsername(username);
        user.setEmail(email);
        return UserConverter.toResponse(userRepository.save(user));
    }

    @Transactional
    public UserInfoResponse changeUserInfo(UserUpdateRequest dto, String username) {
        User user = findUserByUsername(username);
        user.setEmail(dto.getEmail());
        user.setPhoneNum(dto.getPhoneNum());
        return UserConverter.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserGetRequest findUserInfo(String username) {
        User user = findUserByUsername(username);
        // TODO: 유저가 멘토인지 멘티인지 구분 후 파트와 동아리 넣어줘야됨
        return userRepository.findUserInfoByUsername(username);
        // return UserGetUpdateDto.toDto(user);
    }

    @Transactional
    public void deleteUser(String username) {
        User user = findUserByUsername(username);
        userRepository.delete(user);
    }

    @Transactional
    public void saveMobileUser(MobileUserDto dto) {
        if (!userRepository.findByUsernameContaining(dto.getUsername()).isPresent()) {
            userRepository.save(dto.toEntity());
        }
    }
}
