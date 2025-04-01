package com.soongsil.CoffeeChat.domain.repository.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.soongsil.CoffeeChat.domain.dto.UserRequest.*;
import com.soongsil.CoffeeChat.domain.entity.Mentor;
import com.soongsil.CoffeeChat.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    @Query(value = "select * from user where username = :username", nativeQuery = true)
    Optional<User> findByUsernameWithDeleted(@Param("username") String username);

    Optional<User> findByUsername(String username);

    User findByMentor(Mentor mentor);

    User findByMenteeId(Long menteeId);

    UserGetRequest findUserInfoByUsername(String username);
}
