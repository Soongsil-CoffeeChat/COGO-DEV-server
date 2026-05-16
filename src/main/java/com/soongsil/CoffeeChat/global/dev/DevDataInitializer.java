package com.soongsil.CoffeeChat.global.dev;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.soongsil.CoffeeChat.domain.auth.enums.Role;
import com.soongsil.CoffeeChat.domain.mentee.dto.MenteeRequest;
import com.soongsil.CoffeeChat.domain.mentor.dto.MentorRequest;
import com.soongsil.CoffeeChat.domain.mentor.enums.ClubEnum;
import com.soongsil.CoffeeChat.domain.mentor.enums.PartEnum;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Profile("local")
@RequiredArgsConstructor
@Slf4j
public class DevDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        createDevUser("dev_mentor_google_001", "dev_mentor", Role.ROLE_MENTOR);
        createDevUser("dev_mentee_google_001", "dev_mentee", Role.ROLE_MENTEE);
        createDevUser("dev_admin_google_001", "dev_admin", Role.ROLE_ADMIN);
        log.info("[DEV] 개발용 테스트 유저 3명 생성 완료");
    }

    private void createDevUser(String username, String name, Role role) {
        if (userRepository.findByUsernameAndIsDeletedFalse(username).isEmpty()) {
            User user =
                    User.builder()
                            .username(username)
                            .name(name)
                            .email(username + "@dev.local")
                            .role(role)
                            .build();

            if (role == Role.ROLE_MENTOR) {
                user.registerAsMentor(
                        new MentorRequest.MentorJoinRequest(PartEnum.BE, ClubEnum.GDGoC));
            }
            if (role == Role.ROLE_MENTEE) {
                user.registerAsMentee(new MenteeRequest.MenteeJoinRequest(PartEnum.BE));
            }

            userRepository.save(user);
        }
    }
}
