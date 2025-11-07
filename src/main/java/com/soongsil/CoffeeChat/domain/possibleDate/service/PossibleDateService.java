package com.soongsil.CoffeeChat.domain.possibleDate.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.domain.mentor.entity.Mentor;
import com.soongsil.CoffeeChat.domain.mentor.repository.MentorRepository;
import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateConverter;
import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateRequest.PossibleDateCreateUpdateRequest;
import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateResponse.PossibleDateCreateUpdateResponse;
import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateResponse.PossibleDateDetailResponse;
import com.soongsil.CoffeeChat.domain.possibleDate.entity.PossibleDate;
import com.soongsil.CoffeeChat.domain.possibleDate.repository.PossibleDateRepository;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class PossibleDateService {
    private final PossibleDateRepository possibleDateRepository;
    private final UserRepository userRepository;
    private final MentorRepository mentorRepository;

    private User findUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
    }

    // possibleDate 오늘 날짜로부터 2주 이내인지 검증
    private void validateDate(LocalDate date) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        // 2주 이내
        LocalDate maxWeek = today.plusWeeks(2);
        if (date.isBefore(today) || date.isAfter(maxWeek))
            throw new GlobalException(GlobalErrorCode.INVALID_DATE_RANGE);
    }

    // possibleDate 시간 입력 검증
    private void validateTime(LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime))
            throw new GlobalException(GlobalErrorCode.INVALID_TIME_RANGE);
    }

    @Transactional(readOnly = true)
    public List<PossibleDateDetailResponse> findPossibleDateListByMentor(Long mentorId) {
        // 2주로 설정
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate twoWeeksLater = today.plusWeeks(2);

        List<PossibleDateDetailResponse> result= possibleDateRepository.getPossibleDatesByMentorId(mentorId).stream()
                .filter(
                        possibleDate ->
                                !possibleDate.getDate().isBefore(today)
                                        && !possibleDate.getDate().isAfter(twoWeeksLater))
                .map(PossibleDateConverter::toResponse)
                .collect(Collectors.toList());
        log.debug("findSlots mentorId={}, range=[{} ~ {}], result={}",
                mentorId, today, twoWeeksLater,result.size());
        return result;
    }

    @Transactional(readOnly = true)
    public List<PossibleDateDetailResponse> findMentorPossibleDateListByUsername(String username) {
        User user = findUserByUsername(username);
        if(!user.isMentor()){
            throw new GlobalException(GlobalErrorCode.MENTOR_NOT_FOUND);
        }

        return findPossibleDateListByMentor(user.getMentor().getId());
    }

    @Transactional
    public PossibleDateCreateUpdateResponse createPossibleDate(
            PossibleDateCreateUpdateRequest request, String userName) {
        User user = findUserByUsername(userName);
        Mentor mentor =
                mentorRepository
                        .findById(user.getMentor().getId())
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.MENTOR_NOT_FOUND));

        PossibleDate possibleDate = PossibleDateConverter.toEntity(request, mentor);

        possibleDateRepository.save(possibleDate);
        return PossibleDateConverter.toCreateUpdateResponse(possibleDate);
    }

    @Transactional
    public PossibleDateCreateUpdateResponse updatePossibleDate(
            Long possibleDateId, PossibleDateCreateUpdateRequest request, String userName) {
        User user = findUserByUsername(userName);
        Mentor mentor =
                mentorRepository
                        .findById(user.getMentor().getId())
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.MENTOR_NOT_FOUND));

        // 본인 소유 PossibleDate 조회
        PossibleDate possibleDate =
                possibleDateRepository
                        .findByIdAndMentor_Id(possibleDateId, mentor.getId())
                        .orElseThrow(
                                () -> new GlobalException(GlobalErrorCode.POSSIBLE_DATE_NOT_FOUND));

        validateDate(possibleDate.getDate());
        validateTime(possibleDate.getStartTime(), possibleDate.getEndTime());

        PossibleDateConverter.updateEntity(possibleDate, request);
        return PossibleDateConverter.toCreateUpdateResponse(possibleDate);
    }
}
