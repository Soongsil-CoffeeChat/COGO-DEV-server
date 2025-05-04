package com.soongsil.CoffeeChat.domain.report.service;

import com.soongsil.CoffeeChat.domain.report.dto.ReportDto;
import com.soongsil.CoffeeChat.domain.report.entity.Report;
import com.soongsil.CoffeeChat.domain.report.repository.ReportRepository;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReportDto createReportMentor(ReportDto request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
        return ReportDto.from(reportRepository.save(Report.from(request, user.getId())));
    }

    @Transactional
    public ReportDto createReportMentee(ReportDto request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
        return ReportDto.from(reportRepository.save(Report.from(request, user.getId())));
    }
}
