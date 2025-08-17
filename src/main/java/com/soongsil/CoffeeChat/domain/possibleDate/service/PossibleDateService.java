package com.soongsil.CoffeeChat.domain.possibleDate.service;

import com.soongsil.CoffeeChat.domain.mentor.entity.Mentor;
import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateConverter;
import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateRequest.PossibleDateCreateRequest;
import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateResponse.PossibleDateCreateResponse;
import com.soongsil.CoffeeChat.domain.possibleDate.entity.PossibleDate;
import com.soongsil.CoffeeChat.domain.possibleDate.repository.PossibleDateRepository;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

        possibleDateRepository.deleteAllByMentor(mentor);

        List<PossibleDate> possibleDates =
                dtos.stream()
                        .map(it -> PossibleDateConverter.toEntity(it, mentor))
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
                .map(PossibleDateConverter::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PossibleDateCreateResponse> findMentorPossibleDateListByUsername(String username) {
        User user = findUserByUsername(username);
        return findPossibleDateListByMentor(user.getMentor().getId());
    }
}
