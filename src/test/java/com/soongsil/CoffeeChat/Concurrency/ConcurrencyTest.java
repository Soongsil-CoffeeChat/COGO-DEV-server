package com.soongsil.CoffeeChat.Concurrency;

import com.soongsil.CoffeeChat.entity.Application;
import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.PossibleDate;
import com.soongsil.CoffeeChat.repository.MenteeRepository;
import com.soongsil.CoffeeChat.repository.Mentor.MentorRepository;
import com.soongsil.CoffeeChat.repository.PossibleDate.PossibleDateRepository;
import com.soongsil.CoffeeChat.service.ApplicationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ConcurrencyTest {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private PossibleDateRepository possibleDateRepository;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private MenteeRepository menteeRepository;

    @Test
    public void testConcurrency() throws InterruptedException {
        Long possibleDateId = 2002L; // 테스트할 PossibleDate ID
        Mentor mentor = mentorRepository.findById(1L).orElseThrow(); // 테스트할 Mentor
        Mentee mentee = menteeRepository.findById(1L).orElseThrow(); // 테스트할 Mentee

        // 사전 데이터 세팅
        setupTestData();

        Thread thread1 = new Thread(() -> {
            try {
                Application application = applicationService.createApplicationIfPossible(possibleDateId, mentor, mentee);
                System.out.println("Thread 1: " + application);
            } catch (Exception e) {
                System.out.println("Thread 1: " + e.getMessage());
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                Application application = applicationService.createApplicationIfPossible(possibleDateId, mentor, mentee);
                System.out.println("Thread 2: " + application);
            } catch (Exception e) {
                System.out.println("Thread 2: " + e.getMessage());
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }

    @Transactional
    public void setupTestData() {
        PossibleDate possibleDate = PossibleDate.builder()
                .id(2002L)
                .mentor(Mentor.builder().id(1L).build()) // 가상의 Mentor 객체를 생성하여 설정
                .date(LocalDate.of(2024, 6, 25))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .isActive(true)
                .build();
        possibleDateRepository.save(possibleDate);
    }
}
