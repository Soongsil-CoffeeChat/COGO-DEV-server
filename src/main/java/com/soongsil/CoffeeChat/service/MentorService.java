package com.soongsil.CoffeeChat.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.dto.MentorConverter;
import com.soongsil.CoffeeChat.dto.MentorRequest.MentorIntroductionUpdateRequest;
import com.soongsil.CoffeeChat.dto.MentorRequest.MentorUpdateRequest;
import com.soongsil.CoffeeChat.dto.MentorResponse.MentorGetUpdateDetailResponse;
import com.soongsil.CoffeeChat.dto.MentorResponse.MentorIntroductionGetUpdateResponse;
import com.soongsil.CoffeeChat.dto.MentorResponse.MentorListResponse;
import com.soongsil.CoffeeChat.entity.Introduction;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.entity.enums.ClubEnum;
import com.soongsil.CoffeeChat.entity.enums.PartEnum;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import com.soongsil.CoffeeChat.repository.Mentor.MentorRepository;
import com.soongsil.CoffeeChat.repository.PossibleDate.PossibleDateRepository;
import com.soongsil.CoffeeChat.repository.User.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MentorService {
    private final MentorRepository mentorRepository;
    private final UserRepository userRepository;
    private final PossibleDateRepository possibleDateRepository;

    @Transactional(readOnly = true)
    public List<MentorListResponse> getMentorDtoListByPart(PartEnum part) {
        return mentorRepository.getMentorListByPart(part); // 일반join
    }

    @Transactional(readOnly = true)
    public List<MentorListResponse> getMentorDtoListByClub(ClubEnum club) {
        return mentorRepository.getMentorListByClub(club); // 일반join
    }

    @Transactional(readOnly = true)
    public List<MentorListResponse> getMentorDtoListByPartAndClub(PartEnum part, ClubEnum club) {
        return mentorRepository.getMentorListByPartAndClub(part, club);
    }

    private User findUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public MentorGetUpdateDetailResponse getMentorDtoById(Long mentorId) {
        // TODO: join으로 바꾸면될듯
        Mentor findMentor =
                mentorRepository
                        .findById(mentorId)
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.MENTOR_NOT_FOUND));
        return MentorConverter.toMentorGetUpdateDetailDto(
                findMentor, userRepository.findByMentor(findMentor));
    }

    public MentorGetUpdateDetailResponse getMentorDtoByIdWithJoin(Long mentorId) {
        return mentorRepository.getMentorInfoByMentorId(mentorId);
    }

    @Transactional
    public MentorGetUpdateDetailResponse updateMentorInfo(
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
    public MentorIntroductionGetUpdateResponse updateMentorIntroduction(
            String userName, MentorIntroductionUpdateRequest dto) {
        User findUser =
                userRepository
                        .findByUsername(userName)
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
        Introduction findMentorIntroduction =
                mentorRepository
                        .findById(findUser.getMentor().getId())
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND))
                        .getIntroduction();

        findMentorIntroduction.updateIntroduction(dto);

        return MentorConverter.toMentorIntroductionGetUpdateResponse(findMentorIntroduction);
    }

    @Transactional(readOnly = true)
    public MentorIntroductionGetUpdateResponse getMentorIntroduction(String username) {
        User findUser = userRepository.findByUsername(username).orElseThrow();
        Introduction introduction = findUser.getMentor().getIntroduction();
        return MentorConverter.toMentorIntroductionGetUpdateResponse(introduction);
    }
}
