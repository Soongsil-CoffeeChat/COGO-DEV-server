package com.soongsil.CoffeeChat.repository.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.dto.UserRequest.*;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameContaining(String usename);

    User findByMentor(Mentor mentor);

    User findByMentorIdWithFetch(Long mentorId);

    User findByUsernameWithFetch(String username);

    User findByMenteeId(Long menteeId);

    UserGetRequest findUserInfoByUsername(String username);
}
