package com.soongsil.CoffeeChat.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.domain.dto.MenteeConverter;
import com.soongsil.CoffeeChat.domain.dto.MenteeRequest.MenteeJoinRequest;
import com.soongsil.CoffeeChat.domain.dto.MenteeResponse.MenteeInfoResponse;
import com.soongsil.CoffeeChat.domain.dto.MentorConverter;
import com.soongsil.CoffeeChat.domain.dto.MentorRequest.MentorJoinRequest;
import com.soongsil.CoffeeChat.domain.dto.MentorResponse.MentorInfoResponse;
import com.soongsil.CoffeeChat.domain.dto.UserConverter;
import com.soongsil.CoffeeChat.domain.dto.UserRequest.UserGetRequest;
import com.soongsil.CoffeeChat.domain.dto.UserRequest.UserJoinRequest;
import com.soongsil.CoffeeChat.domain.dto.UserRequest.UserUpdateRequest;
import com.soongsil.CoffeeChat.domain.dto.UserResponse.UserInfoResponse;
import com.soongsil.CoffeeChat.domain.entity.Mentee;
import com.soongsil.CoffeeChat.domain.entity.Mentor;
import com.soongsil.CoffeeChat.domain.entity.User;
import com.soongsil.CoffeeChat.domain.repository.User.UserRepository;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import com.soongsil.CoffeeChat.infra.sms.SmsUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
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
        user.updateNameAndPhoneNum(dto.getName(), dto.getPhoneNum());
        return UserConverter.toResponse(user);
    }

    @Transactional
    public MentorInfoResponse registerMentor(String username, MentorJoinRequest dto) {
        User user = findUserByUsername(username);
        Mentor mentor = user.registerAsMentor(dto);
        return MentorConverter.toResponse(mentor);
    }

    @Transactional
    public MenteeInfoResponse registerMentee(String username, MenteeJoinRequest dto) {
        User user = findUserByUsername(username);
        Mentee mentee = user.registerAsMentee(dto);
        return MenteeConverter.toResponse(mentee);
    }

    public String send2FACode(String to) {
        return smsUtil.send2FACode(to);
    }

    @Transactional
    public UserInfoResponse updateUser(UserUpdateRequest request, String username) {
        User user = findUserByUsername(username);
        user.updateUser(request);
        return UserConverter.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserGetRequest getUser(String username) {
        // TODO: 유저가 멘토인지 멘티인지 구분 후 파트와 동아리 넣어줘야됨
        return userRepository.findUserInfoByUsername(username);
    }

    @Transactional
    public void deleteUser(String username) {
        User user = findUserByUsername(username);
        userRepository.delete(user);
    }
}
