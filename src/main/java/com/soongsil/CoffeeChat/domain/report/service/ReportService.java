package com.soongsil.CoffeeChat.domain.report.service;

import com.soongsil.CoffeeChat.domain.report.dto.ReportResponse.ReportCreateResponse;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.domain.report.dto.ReportRequest.ReportCreateRequest;
import com.soongsil.CoffeeChat.domain.report.entity.Report;
import com.soongsil.CoffeeChat.domain.report.repository.ReportRepository;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    private User findUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public ReportCreateResponse createReport(String username, ReportCreateRequest request) {
        User user = findUserByUsername(username);
        Report report = ReportConverter.toEntity(request);

        reportRepository.save(report);
        return ReportConverter.toResponse(report);
    }
}
