package com.soongsil.CoffeeChat.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.dto.ReportDto;
import com.soongsil.CoffeeChat.entity.Report;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.repository.ReportRepository;
import com.soongsil.CoffeeChat.repository.User.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReportDto createReportMentor(ReportDto request, String username) throws Exception {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return ReportDto.from(reportRepository.save(Report.from(request, user.get().getId())));
        } else throw new Exception();
    }

    @Transactional
    public ReportDto createReportMentee(ReportDto request, String username) throws Exception {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return ReportDto.from(reportRepository.save(Report.from(request, user.get().getId())));
        } else throw new Exception();
    }
}
