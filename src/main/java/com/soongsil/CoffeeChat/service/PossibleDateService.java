package com.soongsil.CoffeeChat.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.dto.PossibleDateConverter;
import com.soongsil.CoffeeChat.dto.PossibleDateRequest.*;
import com.soongsil.CoffeeChat.dto.PossibleDateResponse.*;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.PossibleDate;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import com.soongsil.CoffeeChat.repository.PossibleDate.PossibleDateRepository;
import com.soongsil.CoffeeChat.repository.User.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PossibleDateService {
    private final PossibleDateRepository possibleDateRepository;
    private final UserRepository userRepository;

    private User findUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public List<PossibleDateCreateResponse> updatePossibleDate(
            List<PossibleDateCreateRequest> dtos, String username) {

        User user = findUserByUsername(username);
        Mentor mentor = user.getMentor();

        // 가능 시간을 갱신하기 위해 모든 가능 시간을 삭제 후 새로운 값 삽입
        // possibleDateRepository.deleteAllByMentor(mentor);
        // log.info("[*] 멘토(" + username + ")의 가능시간 모두 삭제(가능시간 갱신 API 일부)");

        List<PossibleDate> possibleDates =
                dtos.stream()
                        .map(
                                dto -> {
                                    PossibleDate possibleDate = PossibleDateConverter.toEntity(dto);
                                    possibleDate.setMentor(mentor);
                                    mentor.addPossibleDate(possibleDate);
                                    return possibleDate;
                                })
                        .collect(Collectors.toList());

        possibleDateRepository.saveAll(possibleDates);

        return possibleDates.stream()
                .map(PossibleDateConverter::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PossibleDateCreateResponse> findPossibleDateListByMentor(Long mentorId) {
        // 2주로 설정
        LocalDate today = LocalDate.now();
        LocalDate twoWeeksLater = today.plusWeeks(2);

        return possibleDateRepository.getPossibleDatesByMentorId(mentorId).stream()
                .filter(
                        possibleDate ->
                                !possibleDate.getDate().isBefore(today)
                                        && !possibleDate.getDate().isAfter(twoWeeksLater))
                .map(
                        possibleDate ->
                                PossibleDateCreateResponse.builder()
                                        .date(possibleDate.getDate())
                                        .startTime(possibleDate.getStartTime())
                                        .endTime(possibleDate.getEndTime())
                                        .possibleDateId(possibleDate.getId())
                                        .isActive(possibleDate.isActive())
                                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PossibleDateCreateResponse> findMentorPossibleDateListByUsername(String username) {

        User user = findUserByUsername(username);
        return findPossibleDateListByMentor(user.getMentor().getId());
    }

    @Transactional
    public String deletePossibleDate(Long possibleDateId, String username) {
        // ID가 존재하는지 확인하고 예외 처리
        if (!possibleDateRepository.existsById(possibleDateId)) {
            throw new IllegalArgumentException(
                    "The specified possibleDateId does not exist: " + possibleDateId);
        }

        // 삭제 수행
        possibleDateRepository.deleteById(possibleDateId);

        // 삭제 후 성공 여부 반환 (일반적으로 예외가 없으면 삭제 성공)
        return "삭제완료";
    }
}
