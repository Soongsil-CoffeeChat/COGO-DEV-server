package com.soongsil.CoffeeChat.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.domain.dto.MentorConverter;
import com.soongsil.CoffeeChat.domain.dto.MentorRequest.MentorIntroductionUpdateRequest;
import com.soongsil.CoffeeChat.domain.dto.MentorRequest.MentorUpdateRequest;
import com.soongsil.CoffeeChat.domain.dto.MentorResponse.MentorDetailResponse;
import com.soongsil.CoffeeChat.domain.dto.MentorResponse.MentorIntroductionResponse;
import com.soongsil.CoffeeChat.domain.dto.MentorResponse.MentorListResponse;
import com.soongsil.CoffeeChat.domain.entity.Introduction;
import com.soongsil.CoffeeChat.domain.entity.User;
import com.soongsil.CoffeeChat.domain.entity.enums.ClubEnum;
import com.soongsil.CoffeeChat.domain.entity.enums.PartEnum;
import com.soongsil.CoffeeChat.domain.repository.Mentor.MentorRepository;
import com.soongsil.CoffeeChat.domain.repository.User.UserRepository;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MentorService {
    private final MentorRepository mentorRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<MentorListResponse> getMentorList(PartEnum part, ClubEnum club) {
        return mentorRepository.getMentorListByPartAndClub(part, club);
    }

    private User findUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public MentorDetailResponse getMentorDtoByIdWithJoin(Long mentorId) {
        return mentorRepository.getMentorInfoByMentorId(mentorId);
    }

    @Transactional
    public MentorDetailResponse updateMentorInfo(
            String username, MentorUpdateRequest mentorUpdateRequest) {
        User findMentorUser = findUserByUsername(username);
        User updatedMentorUser =
                User.builder()
                        .id(findMentorUser.getId())
                        .name(mentorUpdateRequest.getMentorName())
                        .email(mentorUpdateRequest.getMentorEmail())
                        .role(findMentorUser.getRole())
                        .phoneNum(mentorUpdateRequest.getMentorPhoneNumber())
                        .picture(findMentorUser.getPicture())
                        .build();
        userRepository.save(updatedMentorUser);
        return null;
    }

    @Transactional
    public MentorIntroductionResponse updateMentorIntroduction(
            String userName, MentorIntroductionUpdateRequest dto) {
        User findUser = findUserByUsername(userName);
        Introduction findMentorIntroduction = findUser.getMentor().getIntroduction();
        findMentorIntroduction.updateIntroduction(dto);
        return MentorConverter.toIntroductionResponse(findMentorIntroduction);
    }

    @Transactional(readOnly = true)
    public MentorIntroductionResponse getMentorIntroduction(String username) {
        User findUser = userRepository.findByUsername(username).orElseThrow();
        Introduction introduction = findUser.getMentor().getIntroduction();
        return MentorConverter.toIntroductionResponse(introduction);
    }
}
