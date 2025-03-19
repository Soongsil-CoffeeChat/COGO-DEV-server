package com.soongsil.CoffeeChat.service;

import static com.soongsil.CoffeeChat.controller.exception.enums.MentorErrorCode.*;
import static com.soongsil.CoffeeChat.controller.exception.enums.UserErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.controller.exception.CustomException;
import com.soongsil.CoffeeChat.dto.*;
import com.soongsil.CoffeeChat.dto.MentorRequest.*;
import com.soongsil.CoffeeChat.dto.MentorResponse.*;
import com.soongsil.CoffeeChat.entity.Introduction;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.enums.ClubEnum;
import com.soongsil.CoffeeChat.enums.PartEnum;
import com.soongsil.CoffeeChat.repository.Mentor.MentorRepository;
import com.soongsil.CoffeeChat.repository.PossibleDate.PossibleDateRepository;
import com.soongsil.CoffeeChat.repository.User.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorService {
    private final MentorRepository mentorRepository;
    private final UserRepository userRepository;
    private final PossibleDateRepository possibleDateRepository;

    public List<MentorGetListResponseDto> getMentorDtoListByPart(PartEnum part) {
        return mentorRepository.getMentorListByPart(part); // 일반join
    }

    public List<MentorGetListResponseDto> getMentorDtoListByClub(ClubEnum club) {
        return mentorRepository.getMentorListByClub(club); // 일반join
    }

    public List<MentorGetListResponseDto> getMentorDtoListByPartAndClub(
            PartEnum part, ClubEnum club) {
        return mentorRepository.getMentorListByPartAndClub(part, club);
    }

    private User findUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(
                        () ->
                                new CustomException(
                                        USER_NOT_FOUND.getHttpStatusCode(),
                                        USER_NOT_FOUND.getErrorMessage()));
    }

    public MentorGetUpdateDetailDto getMentorDtoById(Long mentorId) {
        // TODO: join으로 바꾸면될듯
        Mentor findMentor =
                mentorRepository
                        .findById(mentorId)
                        .orElseThrow(
                                () ->
                                        new CustomException(
                                                MENTOR_NOT_FOUND.getHttpStatusCode(),
                                                MENTOR_NOT_FOUND.getErrorMessage()));
        return MentorConverter.toMentorGetUpdateDetailDto(
                findMentor, userRepository.findByMentor(findMentor));
    }

    public MentorGetUpdateDetailDto getMentorDtoByIdWithJoin(Long mentorId) {
        return mentorRepository.getMentorInfoByMentorId(mentorId);
    }

    @Transactional
    public MentorGetUpdateDetailDto updateMentorInfo(
            String username, MentorUpdateRequestDto mentorUpdateRequestDto) {
        User findMentorUser = findUserByUsername(username);
        User updatedMentorUser =
                User.builder()
                        .id(findMentorUser.getId())
                        .name(mentorUpdateRequestDto.getMentorName())
                        .email(mentorUpdateRequestDto.getMentorEmail())
                        .role(findMentorUser.getRole())
                        .phoneNum(mentorUpdateRequestDto.getMentorPhoneNumber())
                        .picture(findMentorUser.getPicture())
                        .build();
        userRepository.save(updatedMentorUser);
        return null;
    }

    @Transactional
    public MentorIntroductionGetUpdateResponse updateMentorIntroduction(
            String userName, MentorIntroductionUpdateRequest dto) {
        User findUser =
                userRepository
                        .findByUsername(userName)
                        .orElseThrow(
                                () ->
                                        new CustomException(
                                                USER_NOT_FOUND.getHttpStatusCode(),
                                                USER_NOT_FOUND.getErrorMessage()));
        Introduction findMentorIntroduction =
                mentorRepository
                        .findById(findUser.getMentor().getId())
                        .orElseThrow(
                                () ->
                                        new CustomException(
                                                MENTOR_NOT_FOUND.getHttpStatusCode(),
                                                MENTOR_NOT_FOUND.getErrorMessage()))
                        .getIntroduction();

        findMentorIntroduction.updateIntroduction(dto);

        return MentorConverter.toMentorIntroductionGetUpdateResponse(findMentorIntroduction);
    }

    public MentorIntroductionGetUpdateResponse getMentorIntroduction(String username) {
        User findUser = userRepository.findByUsername(username).orElseThrow();
        Introduction introduction = findUser.getMentor().getIntroduction();
        return MentorConverter.toMentorIntroductionGetUpdateResponse(introduction);
    }
}
