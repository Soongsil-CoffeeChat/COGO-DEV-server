package com.soongsil.CoffeeChat.PostTestData;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import com.soongsil.CoffeeChat.domain.mentor.repository.MentorRepository;
import com.soongsil.CoffeeChat.domain.possibleDate.repository.PossibleDateRepository;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;
import com.soongsil.CoffeeChat.domain.user.service.UserService;

@SpringBootTest
@Transactional
@Commit
public class setUserMentorTestData {
    @Autowired private MentorRepository mentorRepository;
    @Autowired private PossibleDateRepository possibleDateRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;
    /*
        @Test
        public void testCreate500UsersWithMentors() {
            IntStream.range(0, 500).forEach(i -> {
                String username = "user" + i;
                // User 객체 생성
                User user = new User();
                user.setUsername(username);
                user.setRole("ROLE_USER"); // 초기 역할 설정

                // User 객체 저장
                userRepository.save(user);

                // CreateMentorRequest DTO 생성
                CreateMentorRequest mentorRequest = new CreateMentorRequest();
                mentorRequest.setPhoneNum("010-0000-000" + i);
                mentorRequest.setBirth("1990-01-01");
                mentorRequest.setPart("BE");

                // saveMentorInformation 메소드 호출
                userService.saveMentorInformation(username, mentorRequest);
            });

            // 사용자와 멘토 생성 확인
            long userCount = userRepository.count();
            assert(userCount == 500);
        }

    /*
        //멘토 500개생성
        @Test
        public void add500Mentors() {
            IntStream.range(0, 500).forEach(i -> {
                Mentor mentor = Mentor.builder()
                        .phoneNum("010-1234-" + String.format("%04d", i))
                        .birth("1990-01-01")
                        .part("BE")
                        .build();
                mentorRepository.save(mentor);
            });
        }

        @Test
        @Transactional
        public void addPossibleDatesForMentors() {
            LongStream.range(1, 501).forEach(mentorId -> {
                Optional<Mentor> mentorOptional = mentorRepository.findById(mentorId);
                if (mentorOptional.isPresent()) {
                    Mentor mentor = mentorOptional.get();
                    for (int i = 0; i < 3; i++) {
                        dto dto = dto.builder()
                                .mentor(mentor)
                                .date(LocalDate.now().plusDays(i))
                                .startTime(LocalTime.of(10, 0).plusHours(i))
                                .endTime(LocalTime.of(11, 0).plusHours(i))
                                .apply(false)
                                .build();
                        possibleDateRepository.save(dto);
                    }
                }
            });
        }
    */

}
