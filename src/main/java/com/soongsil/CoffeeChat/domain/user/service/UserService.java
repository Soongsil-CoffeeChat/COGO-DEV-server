package com.soongsil.CoffeeChat.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.soongsil.CoffeeChat.domain.mentee.dto.MenteeConverter;
import com.soongsil.CoffeeChat.domain.mentee.dto.MenteeRequest.MenteeJoinRequest;
import com.soongsil.CoffeeChat.domain.mentee.dto.MenteeResponse.MenteeInfoResponse;
import com.soongsil.CoffeeChat.domain.mentee.entity.Mentee;
import com.soongsil.CoffeeChat.domain.mentor.dto.MentorConverter;
import com.soongsil.CoffeeChat.domain.mentor.dto.MentorRequest.MentorJoinRequest;
import com.soongsil.CoffeeChat.domain.mentor.dto.MentorResponse.MentorInfoResponse;
import com.soongsil.CoffeeChat.domain.mentor.entity.Mentor;
import com.soongsil.CoffeeChat.domain.user.dto.UserConverter;
import com.soongsil.CoffeeChat.domain.user.dto.UserRequest.UserGetRequest;
import com.soongsil.CoffeeChat.domain.user.dto.UserRequest.UserUpdateRequest;
import com.soongsil.CoffeeChat.domain.user.dto.UserResponse.User2FACodeResponse;
import com.soongsil.CoffeeChat.domain.user.dto.UserResponse.UserInfoResponse;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import com.soongsil.CoffeeChat.infra.aws.s3.service.AmazonS3Service;
import com.soongsil.CoffeeChat.infra.sms.SmsUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final SmsUtil smsUtil;
    private final AmazonS3Service s3Service;

    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
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

    public User2FACodeResponse send2FACode(String to) {
        return UserConverter.to2FACodeResponse(smsUtil.send2FACode(to));
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
        user.softDelete();
    }

    @Transactional
    public UserInfoResponse uploadPicture(MultipartFile image, String username) {
        User user = findUserByUsername(username);
        String picture =
                user.getPicture() == null
                        ? s3Service.uploadFile(image, "user")
                        : s3Service.updateFile(user.getPicture(), image, "user");
        user.updatePicture(picture);
        return UserConverter.toResponse(user);
    }
}
