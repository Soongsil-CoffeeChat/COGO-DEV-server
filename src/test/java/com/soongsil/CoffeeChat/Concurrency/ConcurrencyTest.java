package com.soongsil.CoffeeChat.Concurrency;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.domain.application.service.ApplicationService;
import com.soongsil.CoffeeChat.domain.mentee.repository.MenteeRepository;
import com.soongsil.CoffeeChat.domain.mentor.repository.MentorRepository;
import com.soongsil.CoffeeChat.domain.possibleDate.entity.PossibleDate;
import com.soongsil.CoffeeChat.domain.possibleDate.repository.PossibleDateRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ConcurrencyTest {

    @Autowired private ApplicationService applicationService;

    @Autowired private PossibleDateRepository possibleDateRepository;

    @Autowired private MentorRepository mentorRepository;

    @Autowired private MenteeRepository menteeRepository;

    // @DisplayName("동시성 처리 후 테스트")
    // @Test
    // public void testConcurrencyWithRedis() throws InterruptedException {
    //     Long mentorId = 1L; // 테스트할 Mentor ID
    //     LocalDate date = LocalDate.of(2000, 6, 25);
    //     LocalTime startTime = LocalTime.of(10, 0);
    //     LocalTime endTime = LocalTime.of(11, 0);
    //     String userName = "user0"; // 테스트할 사용자 이름
    //
    //     // 사전 데이터 세팅
    //     setupTestData();
    //
    //     ApplicationCreateRequest request = new ApplicationCreateRequest(date, startTime, endTime,
    // mentorId);
    //
    //     Thread thread1 = new Thread(() -> {
    //         try {
    //             ApplicationCreateResponse response =
    // applicationService.createApplication(request, userName);
    //             System.out.println("Thread 1: " + response);
    //         } catch (Exception e) {
    //             System.out.println("Thread 1: " + e.getMessage());
    //         }
    //     });
    //
    //     Thread thread2 = new Thread(() -> {
    //         try {
    //             ApplicationCreateResponse response =
    // applicationService.createApplication(request, userName);
    //             System.out.println("Thread 2: " + response);
    //         } catch (Exception e) {
    //             System.out.println("Thread 2: " + e.getMessage());
    //         }
    //     });
    //
    //     thread1.start();
    //     thread2.start();
    //
    //     thread1.join();
    //     thread2.join();
    // }
    //    @DisplayName("동시성 처리 안한 상태")
    //    @Test
    //    public void testConcurrency() throws InterruptedException {
    //        Long possibleDateId = 2002L; // 테스트할 dto ID
    //        Mentor mentor = mentorRepository.findById(1L).orElseThrow(); // 테스트할 Mentor
    //        Mentee mentee = menteeRepository.findById(1L).orElseThrow(); // 테스트할 Mentee
    //
    //        // 사전 데이터 세팅
    //        setupTestData();
    //
    //        Thread thread1 = new Thread(() -> {
    //            try {
    //                Application application =
    // applicationService.createApplicationIfPossible(possibleDateId, mentor, mentee);
    //                System.out.println("Thread 1: " + application);
    //            } catch (Exception e) {
    //                System.out.println("Thread 1: " + e.getMessage());
    //            }
    //        });
    //
    //        Thread thread2 = new Thread(() -> {
    //            try {
    //                Application application =
    // applicationService.createApplicationIfPossible(possibleDateId, mentor, mentee);
    //                System.out.println("Thread 2: " + application);
    //            } catch (Exception e) {
    //                System.out.println("Thread 2: " + e.getMessage());
    //            }
    //        });
    //
    //        thread1.start();
    //        thread2.start();
    //
    //        thread1.join();
    //        thread2.join();
    //    }

    @Transactional
    public void setupTestData() {
        PossibleDate possibleDate =
                PossibleDate.builder()
                        .id(2003L)
                        .mentor(mentorRepository.findById(1L).get())
                        .date(LocalDate.of(2000, 6, 25))
                        .startTime(LocalTime.of(10, 0))
                        .endTime(LocalTime.of(11, 0))
                        .isActive(true)
                        .build();
        possibleDateRepository.save(possibleDate);
    }
}
